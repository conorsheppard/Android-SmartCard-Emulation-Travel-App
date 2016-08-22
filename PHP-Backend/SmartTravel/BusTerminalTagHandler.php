<?php
include_once "BusTagHandlerPrepare.php";
include_once "DB_CONNECT.php";
include_once "TIMEZONE.php";

$newBalance = 0;
$cardId = $_POST["cardId"];
$stageCategory = $_POST["stageCategory"];
$dataTransmittionError = false;
if(strcmp($stageCategory, "0") == 0) {
    $journeyCost = COST_STAGE_1to3;
} else if(strcmp($stageCategory, "1") == 0) {
    $journeyCost = COST_STAGE_4to13;
} else if(strcmp($stageCategory, "2") == 0) {
    $journeyCost = COST_STAGE_13andOver;
} else {
    $dataTransmittionError = true;
}

$sqlStatement = mysqli_query($con, "SELECT balance, trip_updated FROM users WHERE unique_id = '$cardId'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}

$balance = $userDetailsArray[0]["balance"];
$trip_updated = $userDetailsArray[0]["trip_updated"];

if(floatval($balance) <= 0) {
    lowBalError();
} else if((!$dataTransmittionError)) { //  && (strtotime($currentDateTime) - strtotime($trip_updated)) > MIN_TRIP_TIME
    $newBalance = floatval($balance) - floatval($journeyCost);
    $statement = mysqli_prepare($con, "UPDATE users SET balance = '$newBalance', account_active = 1, ".
        "trip_updated = '$currentDateTime' WHERE unique_id = '$cardId'");
    mysqli_stmt_execute($statement);
    mysqli_stmt_close($statement);
    echo "Tagged on".PHP_EOL."Current balance is ".$newBalance.PHP_EOL;
} else {
    echo "Already Validated";
}
mysqli_close($con);

function lowBalError() {
    echo "Balance is too low, please top up".PHP_EOL;
}