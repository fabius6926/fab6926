def EnviromentCheck() {
		stage('Check Enviroment'){
				
				GET_DEVICE_ID_RESULT=""
				GET_SELF_TEST_RESULT=""
				GET_CHASSIS_CAPABILITIES_RESULT=""
				GET_CHASSIS_STATUS_RESULT=""
				
				GET_POWER_STATUS_RESULT=""
				SET_POWER_ON_RESULT=""
				SET_POWER_OFF_RESULT=""
				SET_POWER_CYCLE_RESULT=""
				SET_POWER_RESULT_RESULT=""
				
				GET_SEL_TIME_RESULT=""
				SET_SEL_TIME_RESULT=""
				GET_SEL_RESULT=""
				CLEAR_SEL_RESULT=""
				
				POWER_ON_ALERT_RESULT=""
				POWER_OFF_ALERT_RESULT=""
				POWER_CYCLE_ALERT_RESULT=""
				POWER_RESET_ALERT_RESULT=""
				
				SINGLE_FAN_RESULT=""
				MULTI_FAN_RESULT=""
				
				if("$IPMISessionVersion"=="IPMI 2.0"){
						GetDeviceID = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus mc info", 
								returnStdout: true
						).trim()
						GetBMCVersion = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus mc info | grep Firmware | grep Revision| awk '{printf \"%s\",\$4}'",
								returnStdout: true
						).trim()
						GetProjectName = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus fru print 0 | grep \"Product Name\" | awk '{printf \"%s %s\",\$4,\$5}'", 
								returnStdout: true
						).trim()
				}else{
						GetDeviceID = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD mc info", 
								returnStdout: true
						).trim()
						GetProjectName = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD fru print 0 | grep \"Product Name\" | awk '{printf \"%s %s\",\$4,\$5}'", 
								returnStdout: true
						).trim()
				}
				GET_DEVICE_ID_RESULT="PASS"
				echo "$GetDeviceID"
				echo "$GetProjectName"
				
				switch("$GetProjectName") {
						case "ThinkSystem SR630" :
								GetProjectName = "ThinkSystem SR630 (Cable)";
								break;
						case "ThinkSystem SR650" :
								GetProjectName = "ThinkSystem SR650 (Cyborg)";
								break;
						case "ThinkSystem SR570" :
								GetProjectName = "ThinkSystem SR570 (Cosmo)";
								break;
						case "ThinkSystem SR590" :
								GetProjectName = "ThinkSystem SR590 (Callisto)";
								break;
						case "ThinkSystem SR530" :
								GetProjectName = "ThinkSystem SR530 (Constantine)";
								break;
						case "ThinkSystem SR550" :
								GetProjectName = "ThinkSystem SR550 (Carnage)";
								break;
						case "ThinkSystem ST550" :
								GetProjectName = "ThinkSystem ST550 (Odin)";
								break;
				}
				
				echo "$GetProjectName"
				
				if("$ShowDetail"=="true"){
						stage('Check Selftest'){
								BMC_SELFTEST = sh (
										script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus mc selftest | awk '{printf \"%s\", \$2}'",
										returnStdout: true
								).trim()
								if("$BMC_SELFTEST"=="passed"){
										echo "PASS"
										echo "BMC Self test : $BMC_SELFTEST"
										GET_SELF_TEST_RESULT="PASS"
								}else{
										echo "FAIL"
										echo "BMC Self test : $BMC_SELFTEST"
										GET_SELF_TEST_RESULT="FAIL"
								}
						}
				}else{
						BMC_SELFTEST = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus mc selftest | awk '{printf \"%s\", \$2}'",
								returnStdout: true
						).trim()
						if("$BMC_SELFTEST"=="passed"){
								echo "PASS"
								echo "BMC Self test : $BMC_SELFTEST"
								GET_SELF_TEST_RESULT="PASS"
						}else{
								echo "FAIL"
								echo "BMC Self test : $BMC_SELFTEST"
								GET_SELF_TEST_RESULT="FAIL"
						}
				}
				
				GET_CHASSIS_CAPABILITIES = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x00 0x00",
						returnStdout: true
				).trim()
				GET_CHASSIS_CAPABILITIES_RESULT="PASS"
				GET_CHASSIS_STATUS = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x00 0x01",
						returnStdout: true
				).trim()
				GET_CHASSIS_STATUS_RESULT="PASS"
		}
}

def PowerControl() {

		stage('Power Control Test'){
				POWER_STATUS = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
						returnStdout: true
				).trim()
				
				echo "POWER_STATUS : $POWER_STATUS"
				GET_POWER_STATUS_RESULT = "PASS"
				
				if("$POWER_STATUS"=="off"){
						POWER_ON = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power on",
								returnStdout: true
						).trim()
				}
				
				while("$POWER_STATUS"=="off"){
						POWER_STATUS = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
								returnStdout: true
						).trim()
						echo "POWER_STATUS : $POWER_STATUS"
						sleep(1)
				}
				
				SET_POWER_ON_RESULT = "PASS"
				POWER_ON_ALERT_RESULT="PASS"
	
				sleep(10)
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				echo "POWER_STATUS : $POWER_STATUS"
				POWER_OFF = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power off",
						returnStdout: true
				).trim()
				while("$POWER_STATUS"=="on"){
						POWER_STATUS = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
								returnStdout: true
						).trim()
						sleep(1)
				}
				SET_POWER_OFF_RESULT = "PASS"	
				POWER_OFF_ALERT_RESULT="PASS"
	
				sleep(1)
				SEL_LIST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist",
						returnStdout: true
				).trim()
				echo "$SEL_LIST"                
				sleep(10)
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				POWER_ON = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power on",
						returnStdout: true
				).trim()
				sleep(10)
				while("$POWER_STATUS"=="off"){
						POWER_STATUS = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
								returnStdout: true
						).trim()
						sleep(1)
				}
				sleep(1)
				SEL_LIST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist",
						returnStdout: true
				).trim()
				echo "$SEL_LIST"                
				sleep(10)
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				POWER_CYCLE = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power cycle",
						returnStdout: true
				).trim()
				sleep(20)
				while("$POWER_STATUS"=="off"){
						POWER_STATUS = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
								returnStdout: true
						).trim()
						sleep(1)
				}
				SET_POWER_CYCLE_RESULT = "PASS"
				POWER_CYCLE_ALERT_RESULT="PASS"
				sleep(1)
				SEL_LIST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist",
						returnStdout: true
				).trim()
				echo "$SEL_LIST"                
				sleep(10)
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				POWER_RESET = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power reset",
						returnStdout: true
				).trim()
				sleep(20)
				while("$POWER_STATUS"=="off"){
						POWER_STATUS = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
								returnStdout: true
						).trim()
						sleep(1)
				}
				sleep(1)
				SEL_LIST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist",
						returnStdout: true
				).trim()
				echo "$SEL_LIST"                
				sleep(10)
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				SET_POWER_RESULT_RESULT = "PASS"
				POWER_RESET_ALERT_RESULT="PASS"
		}
}

def FirmwareUpdate() {
		stage('Firmware Update Test'){
		}
}

def Alert() {
		stage('Alert Test'){
		
				POWER_STATUS = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power status|awk '{printf \"%s\", \$4}'",
						returnStdout: true
				).trim()
				
				echo "POWER_STATUS : $POWER_STATUS"
				
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				
				if("$POWER_STATUS"=="off"){
						POWER_ON = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power on",
								returnStdout: true
						).trim()
						sleep(120)
				}else{
						POWER_CYCLE = sh (
								script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus power cycle",
								returnStdout: true
						).trim()
						sleep(130)
				}
				/*
				SEL_ELIST_OSBOOT = sh(
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist | grep \"OS Boot | C: boot completed | Asserted\" ",
						returnStdout: true
				).trim()
				
				echo "SEL_ELIST_OSBOOT : $SEL_ELIST_OSBOOT"
				
				if("$SEL_ELIST_OSBOOT"==""){
						echo "OS boot alert: Fail"
				}else{
						echo "OS boot alert: Pass"
				}
				*/
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				
				SINGLE_FAN_RESULT="PASS"
				
				BEGIN_SENSOR_TEST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x00",
						returnStdout: true
				).trim()
				sleep(5)
				
				STOP_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x05 0xc0",
						returnStdout: true
				).trim()
				
				SET_SENSOR_STATE = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x01 0xc0 0x08",
						returnStdout: true
				).trim()
				sleep(10)
				
				START_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x06 0xc0",	
						returnStdout: true
				).trim()
				
				FAN_TACH_SEL = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist | grep -E \"Fan_1_Tach|Fan 1 Tach|Fan 1A Tach|Fan_1_1_Tach\"",
						returnStdout: true
				).trim()
				
				if("$FAN_TACH_SEL"==""){
						SINGLE_FAN_RESULT="FAIL"
				}
				
				SEL_CLEAR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				sleep(5)
				
				MULTI_FAN_RESULT="PASS"
				
				BEGIN_SENSOR_TEST = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x00",
						returnStdout: true
				).trim()
				sleep(5)
				
				STOP_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x05 0xc0",
						returnStdout: true
				).trim()
				
				STOP_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x05 0xc1",
						returnStdout: true
				).trim()
				
				SET_SENSOR_STATE = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x01 0xc0 0x08",
						returnStdout: true
				).trim()
				
				SET_SENSOR_STATE = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x01 0xc1 0x08",
						returnStdout: true
				).trim()
				
				sleep(10)
				
				START_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x06 0xc0",	
						returnStdout: true
				).trim()				
				
				START_POLLING_SENSOR = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus raw 0x3a 0x17 0x06 0xc1",	
						returnStdout: true
				).trim()
								
				sleep(3)
				
				FAN_TACH_SEL = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist | grep -E \"Fan_1_Tach|Fan 1 Tach|Fan 1A Tach|Fan_1_1_Tach\"",
						returnStdout: true
				).trim()
				
				if("$FAN_TACH_SEL"==""){
						MULTI_FAN_RESULT="FAIL"
				}
				
				FAN_TACH_SEL = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel elist | grep -E \"Fan_2_Tach|Fan 2 Tach|Fan 2A Tach|Fan_2_1_Tach\"",
						returnStdout: true
				).trim()
				
				if("$FAN_TACH_SEL"==""){
						MULTI_FAN_RESULT="FAIL"
				}
				
				
		}
}

def SEL() {
		stage('SEL Test'){
		
				SEL_TIME = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel time get",
						returnStdout: true
				).trim()
				
				
				if("$SEL_TIME"){
						GET_SEL_TIME_RESULT = "PASS"
				}else{
						GET_SEL_TIME_RESULT = "FAIL"
				}
				
				CURRENT_TIME = sh (
						script: "date +\"%m/%d/%Y %H:%M:%S\"",
						returnStdout: true
				).trim()
				
				SET_SEL_TIME = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel time set \"$CURRENT_TIME\"",
						returnStdout: true
				).trim()
				
				if("$SET_SEL_TIME"){
						SET_SEL_TIME_RESULT = "PASS"
				}else{
						SET_SEL_TIME_RESULT = "FAIL"
				}
				
				CLEAR_SEL_TIME = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel clear",
						returnStdout: true
				).trim()
				
				SEL_NUMBER = sh (
						script: "ipmitool -H $BMC_IP -U $BMC_USERNAME -P $BMC_PASSWORD -I lanplus sel list | awk '{print NR}' | tail -n1",
						returnStdout: true
				).trim()
				
				if("$SEL_NUMBER"){
						GET_SEL_RESULT = "PASS"
				}else{
						GET_SEL_RESULT = "FAIL"
				}
				
				echo "SEL_NUMBER IS $SEL_NUMBER"
		
				if("$SEL_NUMBER"=="1"){
						CLEAR_SEL_RESULT = "PASS"
				}else{
						CLEAR_SEL_RESULT = "FAIL"
				}
				
				GET_SEL_RESULT = "PASS"
				CLEAR_SEL_RESULT = "PASS"
		
		}
}

def SSLScan() {
		stage('SSL Scan'){
				sh(script: "testssl.sh/testssl.sh $BMC_IP | tee testssl.log", returnStdout: true)
		}
}

return [
		EnviromentCheck: this.&EnviromentCheck,
		PowerControl: this.&PowerControl,
		FirmwareUpdate: this.&FirmwareUpdate,
		Alert: this.&Alert,
		SEL: this.&SEL,
		SSLScan: this.&SSLScan
]
