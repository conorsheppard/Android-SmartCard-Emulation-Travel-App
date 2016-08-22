<?php
include_once "CONSTANT_DEFS.php";
include_once "TIMEZONE.php";
include_once "DB_CONNECT.php";
include_once "TEST_CASE_1.php";

$statement = mysqli_prepare($con, "UPDATE users SET trip_active = '$TRIP_ACTIVE', balance = '$BALANCE', ".
    "last_terminal = '$LAST_TERMINAL', account_active = '$ACCOUNT_ACTIVE ', trip_updated = '$currentDateTime'".
    "WHERE unique_id = '$UNIQUE_ID'")or die(mysqli_error($con));
mysqli_stmt_execute($statement);
mysqli_stmt_close($statement);
mysqli_close($con);

