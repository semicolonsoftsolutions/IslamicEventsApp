
<?php

	include './database.php';
	include './gcm.php';

	if (isset($_POST['event_id'])){
		//broadcast here
		$event_id =  $_POST['event_id'];
		
		$db = new Database();
		$db -> connect();

		$gcm = new Gcm();
		
		$reg_ids = $db ->get_reg_ids();
	    $result = $gcm -> push_message($reg_ids, array("msg" => $db -> get_event($event_id)));
	    $response = json_decode($result,true);
	    //IF SUCCESS IS GREATER THEN 0 THEN IT'S PUSHED BY CLOUD
		if ( $response['success'] > 0 ){
			//MARK THAT EVENT AS PUBLISHED
			$done = $db -> mark_published($event_id);

			if ($done = 1) {
				echo 'Success';
			}

		}

	}

	//SHOW ALL THE EVENTS WITH PUBLISHED = 0 
	$db  = new Database();
	$db -> connect();
	$result = $db -> get_all_events();

	echo '<table border = "1">';
	echo '<tr><th>Event id</th><th>Event Title</th><th>Date</th><th>Address</th><th>Category</th><th>Description</th><th>Action</th></tr>';
	while ($row = mysql_fetch_assoc($result)){
		
		
			echo '<tr>';
				echo '<td>'.$row['event_id'].'</td>'; 
				echo '<td>'.$row['attribute1'].'</td>'; 
				echo '<td>'.$row['attribute2'].'</td>'; 
				echo '<td>'.$row['attribute3'].'</td>'; 
				echo '<td>'.$row['attribute4'].'</td>'; 
				echo '<td>'.$row['description'].'</td>'; 
				echo '<td><a href="'.$row['attribute5'].'"/>Click to View Image</a>'.'</td>';
			    //echo '<img src="'.$row['attribute5'] .'" />
				echo '<td><form method="POST" action=""><input type="submit" value"Broadcast"/> <input name = "event_id" type="hidden" value='. $row['event_id'].' /></form></td>';
			echo '</tr>';

	}
	echo '</table>';

?>