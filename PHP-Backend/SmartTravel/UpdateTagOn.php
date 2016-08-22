<?php
//$con = mysqli_connect("mysql4.000webhost.com", "a4036717_conor", "sheppac2", "a4036717_stusers");
$con = mysqli_connect("localhost", "root", "", "User");
$uniqueId = $_POST["uniqueId"];

$myfile = fopen("debugUpdateTagOn.txt", "a+") or die("Unable to open file!");
fwrite($myfile, "hello\n");

// retrieve balance, trip_start, last_terminal
$statement = mysqli_prepare($con, "SELECT last_terminal FROM users WHERE unique_id = ?");
mysqli_stmt_bind_param($statement, "s", $last_terminal);
mysqli_stmt_execute($statement);
mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement, $last_terminal);

fwrite($myfile, $uniqueId);
$result = mysqli_query("SELECT last_terminal FROM users WHERE unique_id = '$uniqueId'");
$row = mysqli_fetch_row($result);
// array to store user data
$user = array();

// iterate over user details
while(mysqli_stmt_fetch($statement)) {
    //$user[balance] = $balance;
    //$user[$trip_start] = $trip_start;
    $user[$last_terminal] = $last_terminal;
}
fwrite($myfile, "\n user = ");
fwrite($myfile, $last_terminal);

// turns the $user array into a json object so that it can be passed back to the phone
echo json_encode($user);

mysqli_stmt_close($statement);
mysqli_close($con);
fclose($myfile);