<?php
include_once "TIMEZONE.php";
include_once "DB_CONNECT.php";
$lastUpdate;
$currentDateTime = (date("Y-m-d H:i:s"));
$cardId = $_POST["cardId"];
$datetime = date("Y-m-d H:i:s");
$userDetailsArray = array();

$sqlStatement = mysqli_query($con, "SELECT last_terminal, balance, trip_start, trip_updated ".
    "FROM users WHERE unique_id = '$cardId'");
while($r = mysqli_fetch_assoc($sqlStatement)) {
    $userDetailsArray[] = $r;
}

// If statement to check if the user detils are "stale"
if(strcmp($userDetailsArray[0]["trip_start"], "1") == 0) {
    $lastUpdate = $userDetailsArray[0]["trip_updated"];
    // compare current time to the user's last tagged on time, using UNIX timestamps
    if ((strtotime($currentDateTime) - strtotime($lastUpdate)) > 14400) {
        $userDetailsArray[0]["trip_start"] = "-1";
        mysqli_query($con, "UPDATE users SET trip_start = -1 WHERE unique_id = '$cardId'");
    } else {
        // User details not "stale"
    }
}

echo json_encode($userDetailsArray);
$con->close();