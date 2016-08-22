<?php

$con = mysqli_connect("localhost", "root", "", "User");

$email = $_POST["email"];
$password = $_POST["password"];
$unique_id = $_POST["unique_id"];
$balance = "0";

$hash = hashSSHA($password);
$encrypted_password = $hash["encryptedPwordAndSalt"]; // encrypted password
$salt = $hash["salt"]; // salt

// instead of entering the values here, we will pass them in the line below to rule out SQL injection
$statement = mysqli_prepare($con, "INSERT INTO users (email, encrypted_password, unique_id, salt, balance) VALUES (?, ?, ?, ?, ?)");
mysqli_stmt_bind_param($statement, "sssss", $email, $encrypted_password, $unique_id, $salt, $balance);
mysqli_stmt_execute($statement);
mysqli_stmt_close($statement);
mysqli_close($con);

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