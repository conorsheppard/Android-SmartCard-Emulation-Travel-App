<?php
$con = mysqli_connect("localhost", "root", "", "User");

$email = $_POST["email"];
$password = $_POST["password"];
$new_password = $_POST["newPassword"];

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
    $hash = hashSSHA($new_password);
    $encrypted_password = $hash["encryptedPwordAndSalt"]; // encrypted password
    $salt = $hash["salt"]; // salt
    $statement = mysqli_query($con, "UPDATE users SET encrypted_password = '$encrypted_password', salt = '$salt' ".
        "WHERE email = '$email'");
    echo "password reset".PHP_EOL;
} else {
    echo "Current password incorrect".PHP_EOL;
}
echo "finished";

mysqli_close($con);


function checkhashSSHA($salt, $password) {

    $hash = base64_encode(sha1($password.$salt, true).$salt);
    return $hash;
}

function hashSSHA($password) {

    $salt = sha1(rand());
    $salt = substr($salt, 0, 10);
    $encrypted = base64_encode(sha1($password.$salt, true).$salt);
    $hash = array(
        "salt" => $salt,
        "encryptedPwordAndSalt" => $encrypted
    );
    return $hash;
}