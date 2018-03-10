<?php
use Monolog\Logger;
use Monolog\Handler\StreamHandler;

require_once 'vendor/autoload.php';

DB::$dbName = 'todorest';
DB::$user = 'todorest';
DB::$encoding = 'utf8';

DB::$password = 'yB57yewEpuddKtGo';

DB::$error_handler = 'sql_error_handler';
DB::$nonsql_error_handler = 'nonsql_error_handler';

function sql_error_handler($params) {
    global $app, $log;
    $log->err("SQL Error" . $params['error']);
    $log->err(" in query" . $params['query']);
    http_response_code(500);
    header('content-type: application/json');
    echo json_encode("500 - internal error");
    die; // don't want to keep going if a query broke
}

function nonsql_error_handler($params) {
    global $app, $log;
    $log->err("SQL Error" . $params['error']);
//so it wont keep in goole
    http_response_code(500);
    header('content-type: application/json');
    echo json_encode("500 - internal error");
    die; // don't want to keep going if a query broke
}
// Slim creation and setup
$app = new \Slim\Slim();
// create a log channel
$log = new Logger('main');
$log->pushHandler(new StreamHandler('logs/everything.log', Logger::DEBUG));
$log->pushHandler(new StreamHandler('logs/errors.log', Logger::ERROR));

$app->response()->header('content-type', 'application/json');

\Slim\Route::setDefaultConditions(array(
    'id' => '\d+'
));

$app ->notFound(function() use($app){
    $app->response()->status(404);
    echo json_encode("404 - record not found");
});

$app->get('/todos', function() use ($app, $log){
    $todoList = DB::query('SELECT * FROM todos');
    echo json_encode($todoList, JSON_PRETTY_PRINT);
});

$app->get('/todos/:id', function($id) use ($app, $log){
    $todoList = DB::queryFirstRow('SELECT * FROM todos WHERE id = %d', $id);
    if($todoList){
        echo json_encode($todoList, JSON_PRETTY_PRINT);        
    } else{
       $app->notFound();
    }
    
});

$app->post('/todos', function() use ($app, $log){
    $json = $app->request()->getBody();
    //javascript { = object or associative array but in php is different
    //meekrodb uses associative (key value pair) array
    $data = json_decode($json, true); // returns associative array, if false return object
    //todo dont forget the error handling
    $result = isTodoValid($data);
    if ($result == TRUE){
        DB::insert('todos', $data);
        $app->response()->status(201);
        echo json_encode(true);  
    }
    else{
        $log->debug("POST /todos 400:" + $result);
        $app->response()->status(400);
        echo json_encode('400- data invalid' + $result);
    }
    
    
});

$app->delete('/todos/:id', function($id) use ($app, $log){
    //note: deletion always succeeds and returns 200
    // you may echo true/false depending on wheter reourd was acually deleted or not
    
    DB::delete('todos','id=%d', $id);
    $counter = DB::affectedRows();
    echo $counter;
    //echo json_encode(DB::affectedRows() != 0);
});

$app->put('/todos/:id', function($id) use ($app, $log){
    $json = $app->request()->getBody();
    //javascript { = object or associative array but in php is different
    //meekrodb uses associative (key value pair) array
    $data = json_decode($json, true); // returns associative array, if false return object
    $data['id'] = $id; //prevent from id being changed
    $result = isTodoValid($data);
    if ($result == TRUE){
    DB::update('todos', $data, 'id=%d', $id);
    if (DB::affectedRows() > 0){
        echo json_encode(true);
    } else{
        $app->response()->status(400);
        echo json_encode("400 - bad request you dont want to show this to user");
    }
    }else{
         $log->debug("POST /todos 400:" + $result);
        $app->response()->status(400);
        echo json_encode('400- data invalid' + $result);
    }
    

});
//return true if data if good otherwise string describe the problem
function isTodoValid($todo){
    if (is_null($todo)){
        return "JSON parsing failed, todo is null";
    }
    if(count($todo) != 3){
        return "Invalid number of values";
    }
    if(strlen($todo['task']) < 1 || strlen($todo['task'] > 100)){
        return "Task length must be between 1 and 100";
    }
    //alias of: date_create_from_format($format, $time, $object)
    if(DateTime::createFromFormat('Y-m-d', $todo['dueDate']) == FALSE){
        return "due date is invalid must be YYYY-MM-DD";
    }
    if(!($todo['isDone'] == 'done' || $todo['isDone'] == 'pending')){
        return " isdone must be either 'done' or 'ispending'";
    }
    return true;
}
$app->run();