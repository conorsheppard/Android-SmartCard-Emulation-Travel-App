<?php
include_once "DB_CONNECT.php";

$userId = $_POST["cardId"];

$sqlStatement = mysqli_query($con, "SELECT balance FROM users WHERE unique_id = '$userId'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}

$balance = $userDetailsArray[0]["balance"];

echo $balance;
mysqli_close($con);