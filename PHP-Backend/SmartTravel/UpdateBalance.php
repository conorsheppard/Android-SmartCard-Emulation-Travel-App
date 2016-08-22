<?php
include_once "DB_CONNECT.php";

$userId = $_POST["userId"];
$topUpAmount = $_POST["topUpAmount"];

$sqlStatement = mysqli_query($con, "SELECT balance FROM users WHERE unique_id = '$userId'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}

$balance = $userDetailsArray[0]["balance"];
$newBalance = (floatval($topUpAmount)/2) + floatval($balance);
$statement = mysqli_prepare($con, "UPDATE users SET balance = '$newBalance', account_active = 1 WHERE unique_id = '$userId'");
mysqli_stmt_execute($statement);

mysqli_stmt_close($statement);
mysqli_close($con);