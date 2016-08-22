<?php
$con = mysqli_connect("localhost", "root", "", "User");
// Check connection
if ($con->connect_error) {
    die("Connection failed: ".$con->connect_error);
}