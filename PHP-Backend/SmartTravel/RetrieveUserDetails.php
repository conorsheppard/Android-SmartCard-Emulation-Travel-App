<?php
$cardId = $_POST["cardId"];
$currentTermId = $_POST["TERMINAL_ID"];
$turnstile_pos = $_POST["TURNSTILE_POS"];
$userDetailsArray = array();

$sqlStatement = mysqli_query($con, "SELECT * FROM users WHERE unique_id = '$cardId'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}

$id = $userDetailsArray[0]["id"];
$unique_id = $userDetailsArray[0]["unique_id"];
$balance = $userDetailsArray[0]["balance"];
$trip_active = $userDetailsArray[0]["trip_active"];
$trip_updated = $userDetailsArray[0]["trip_updated"];
$last_terminal = $userDetailsArray[0]["last_terminal"];
$email = $userDetailsArray[0]["email"];
$encrypted_password = $userDetailsArray[0]["encrypted_password"];
$salt = $userDetailsArray[0]["salt"];
$created_at = $userDetailsArray[0]["created_at"];
$updated_at = $userDetailsArray[0]["updated_at"];
$account_active = $userDetailsArray[0]["account_active"];