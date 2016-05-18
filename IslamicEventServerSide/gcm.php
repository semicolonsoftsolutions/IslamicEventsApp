<?php
	
	class Gcm {
		
		function __construct() {
         
    	}

		function push_message($registration_ids, $event_id){
			include_once './gcm_config.php';

			//$registration_ids is an array of registration ids and $event_id is also an array

			$fields = array('registration_ids' => $registration_ids,'data' => $event_id,);
 
        	$headers = array('Authorization: key=' . GOOGLE_API_KEY,'Content-Type: application/json');
        	// Open connection
        	$ch = curl_init();
 
        	// Set the url, number of POST vars, POST data
	        curl_setopt($ch, CURLOPT_URL, REQ_URL);
	 
	        curl_setopt($ch, CURLOPT_POST, true);
	        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
	        // Disabling SSL Certificate support temporarly
	        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	 
	        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
	 
	        // Execute post
	        $result = curl_exec($ch);
	        if ($result === FALSE) {
	            die('Curl failed: ' . curl_error($ch));
	        }
	 
	        // Close connection
	        curl_close($ch);
	        return $result;
		}
	}

?>





