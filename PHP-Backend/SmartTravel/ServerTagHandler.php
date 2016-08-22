<?php
//ini_set( "display_errors", "0");
include_once "CONSTANT_DEFS.php";
include_once "TIMEZONE.php";
include_once "DB_CONNECT.php";
include_once "RetrieveUserDetails.php";
include_once "StationLookUpTable.php";

if(is_null($account_active))
    exit("Please purchase credit to start using app");

$lastUpdate = $userDetailsArray[0]["trip_updated"];
switch ($trip_active) {
    // If the user wants to tag on
    case "0":
        // If terminals are the same
        if(strcmp($currentTermId, $last_terminal) == 0) {
            // Then check what the turnstile position is
            if(strcmp($turnstile_pos, TERMINAL_POS_REAR) == 0) {
                // Check the time since last tagi
                alreadyValMessage();
            } else if(strcmp($turnstile_pos, TERMINAL_POS_FRONT) == 0) {
                if(floatval($balance) <= 0) {
                    lowBalError();
                } else {
                    tagOn($balance, $con, $currentTermId, $currentDateTime, $cardId, $userDetailsArray);
                }
            }
            //Debug
            else { echo "If statement uncaught error on line ".__LINE__.PHP_EOL; }
        } else { // TERMINALS ARE DIFFERENT
            // Then check what the turnstile position is
            if(strcmp($turnstile_pos, TERMINAL_POS_REAR) == 0) {
                alreadyValMessage();
            } else if(strcmp($turnstile_pos, TERMINAL_POS_FRONT) == 0) {
                if(floatval($balance) > 0) {
                    tagOn($balance, $con, $currentTermId, $currentDateTime, $cardId, $userDetailsArray);
                } else {
                    lowBalError();
                }
            }
            //Debug
            else { echo "If statement uncaught error on line ".__LINE__.PHP_EOL; }
        }
        break;
    // If the user wants to tag off
    case "1":
        // If terminals are the same
        if(strcmp($currentTermId, $last_terminal) == 0) {
            // Then check what the turnstile position is
            if(strcmp($turnstile_pos, TERMINAL_POS_REAR) == 0) {
                // Check the time since last tag
                if((strtotime($currentDateTime) - strtotime($lastUpdate)) < MAX_CANCEL_TIME) {
                    cancelTransaction($balance, $con, $currentTermId, $currentDateTime, $cardId);
                    echo "Transaction cancelled";
                } else if((strtotime($currentDateTime) - strtotime($lastUpdate)) < MAX_TRIP_TIME) {
                    tagOff($balance, $currentTermId, $last_terminal, $con, $cardId, $currentDateTime, $userDetailsArray, $stationsArray);
                } else if((strtotime($currentDateTime) - strtotime($lastUpdate)) > MAX_TRIP_TIME) {
                    alreadyValMessage();
                }
                // Debug
                else { echo "If statement uncaught error on line ".__LINE__.PHP_EOL; }
            } else if(strcmp($turnstile_pos, TERMINAL_POS_FRONT) == 0) {
                if((strtotime($currentDateTime) - strtotime($lastUpdate)) > MAX_TRIP_TIME) {
                    if(floatval($balance) <= 0) {
                        lowBalError();
                    } else {
                        tagOn($balance, $con, $currentTermId, $currentDateTime, $cardId, $userDetailsArray);
                    }
                } else  {
                    echo "Already validated";
                }
            }
            //Debug
            else { echo "If statement uncaught error on line ".__LINE__.PHP_EOL;}
        } else { // TERMINALS ARE DIFFERENT OR USER'S LAST_TERMINAL HASN'T BEEN INITIALIZED
            // Then check what the turnstile position is
            if(strcmp($turnstile_pos, TERMINAL_POS_REAR) == 0) {
                // Check the time since last tag
                if((strtotime($currentDateTime) - strtotime($lastUpdate)) > MAX_TRIP_TIME) {
                    alreadyValMessage();
                } else {
                    tagOff($balance, $currentTermId, $last_terminal, $con, $cardId, $currentDateTime, $userDetailsArray, $stationsArray);
                }
            } else if(strcmp($turnstile_pos, TERMINAL_POS_FRONT) == 0) {
                if((strtotime($currentDateTime) - strtotime($lastUpdate)) > MAX_TRIP_TIME) {
                    if(floatval($balance) <= 0) {
                        lowBalError();
                    } else {
                        tagOn($balance, $con, $currentTermId, $currentDateTime, $cardId, $userDetailsArray);
                    }
                } else {
                    echo "Journey active, to tag off use rear of turnstile";
                }
            }
        }
        break;
}


//echo json_encode($userDetailsArray);
$con->close();

function lowBalError() {
    echo "Balance is too low, please top up".PHP_EOL;
}

function alreadyValMessage() {
    echo "Already validated, to tag on use front of turnstile";
}

function tagOn($balance, $con, $currentTermId, $currentDateTime, $cardId, $userDetailsArray) {
    $newBalance = floatval($balance)-MAX_COST;
    $sql = mysqli_prepare($con, "UPDATE users SET balance = '$newBalance', last_terminal = '$currentTermId', ".
        "trip_active = ".TRIP_IS_ACTIVE.", trip_updated = '$currentDateTime' WHERE unique_id = '$cardId'") or die(mysqli_error($con));
    mysqli_stmt_execute($sql);
    mysqli_stmt_close($sql);
    $userDetailsArray[0]["balance"] = strval($newBalance);
    $userDetailsArray[0]["last_terminal"] = $currentTermId;
    $userDetailsArray[0]["trip_active"] = "1";
    $userDetailsArray[0]["trip_updated"] = $currentDateTime;
    echo "Tagged On".PHP_EOL;
    echo "Balance: ".$newBalance.PHP_EOL;
}

function tagOff($balance, $currentTermId, $last_terminal, $con, $cardId, $currentDateTime, $userDetailsArray, $stationsArray) {
    $difference = MAX_COST - calcJourneyCost($stationsArray, $currentTermId, $last_terminal);
    $newBalance = $balance+$difference;
    $sql = mysqli_prepare($con, "UPDATE users SET balance = '$newBalance', last_terminal = '$currentTermId', ".
        "trip_active = ".TRIP_NOT_ACTIVE.", trip_updated = '$currentDateTime' WHERE unique_id = '$cardId'") or die(mysqli_error($con));
    mysqli_stmt_execute($sql);
    mysqli_stmt_close($sql);
    $userDetailsArray[0]["balance"] = strval($newBalance);
    $userDetailsArray[0]["last_terminal"] = $currentTermId;
    $userDetailsArray[0]["trip_active"] = "0";
    $userDetailsArray[0]["trip_updated"] = $currentDateTime;
    echo "Tagged off".PHP_EOL;
    echo "new balance = ".$newBalance.PHP_EOL;
}

function cancelTransaction($balance, $con, $currentTermId, $currentDateTime, $cardId) {
    $newBalance = $balance + MAX_COST;
    $sql = mysqli_prepare($con, "UPDATE users SET balance = '$newBalance', last_terminal = '$currentTermId', ".
        "trip_active = ".TRIP_NOT_ACTIVE.", trip_updated = '$currentDateTime' WHERE unique_id = '$cardId'") or die(mysqli_error($con));
    mysqli_stmt_execute($sql);
    mysqli_stmt_close($sql);
}

function calcJourneyCost($stationsArray, $currentTermId, $last_terminal) {
    $numStops = abs($stationsArray[$currentTermId] - $stationsArray[$last_terminal]);
    if ($numStops <= 3) {
        $journeyCost = COST_STAGE_1;
    } else if ($numStops >= 4 && $numStops <= 6) {
        $journeyCost = COST_STAGE_2;
    } else if ($numStops >= 7 && $numStops <= 13) {
        $journeyCost = COST_STAGE_3;
    } else if ($numStops >= 14 && $numStops <= 18) {
        $journeyCost = COST_STAGE_4;
    } else if ($numStops >= 19 && $numStops <= 21) {
        $journeyCost = COST_STAGE_5;
    } else {
        $journeyCost = COST_STAGE_6;
    }

    return $journeyCost;
}