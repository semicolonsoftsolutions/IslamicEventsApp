<?php
	include './database.php';

	if (isset($_POST['event_id'])) {
		$event_id = $_POST['event_id'];
		$db = new Database();
	$db->connect();
	$row = $db->get_event($event_id);
	print_r(json_encode($row));
	}
	

	
?>