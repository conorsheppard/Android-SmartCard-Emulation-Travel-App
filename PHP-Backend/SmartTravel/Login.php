<?php
ini_set( "display_errors", "1");
$con = mysqli_connect("localhost", "root", "", "User");
$email = $_POST["email"];
$password = $_POST["password"];
$encrypted_password;
$salt;

$statement = mysqli_prepare($con, "SELECT encrypted_password, salt FROM users WHERE email = ?");
mysqli_stmt_bind_param($statement, "s", $email);
mysqli_stmt_execute($statement);
mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement, $encrypted_password, $salt);


$userDetailsArray = array();
$sqlStatement = mysqli_query($con, "SELECT encrypted_password, salt FROM users WHERE email = '$email'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}
$encrypted_password = $userDetailsArray[0]["encrypted_password"];
$salt = $userDetailsArray[0]["salt"];


$hash = checkhashSSHA($salt, $password);

if ($encrypted_password == $hash) {
    // user authentication details are correct
    // retrieve user information
    $statement = mysqli_prepare($con, "SELECT email, unique_id FROM users WHERE email = ? AND encrypted_password = ?");
    mysqli_stmt_bind_param($statement, "ss", $email, $encrypted_password);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $email, $unique_id);
    // array to store user data
    $user = array();
    // iterate over user details
    while (mysqli_stmt_fetch($statement)) {
        $user['email'] = $email;
        $user['unique_id'] = $unique_id;
    }

    // turns the $user array into a json object so that it can be passed back to the phone
    echo json_encode($user);
    mysqli_stmt_close($statement);
} else {
    echo "Incorrect details given".PHP_EOL;
}

mysqli_close($con);

function checkhashSSHA($salt, $password) {

    $hash = base64_encode(sha1($password.$salt, true).$salt);
    return $hash;
}