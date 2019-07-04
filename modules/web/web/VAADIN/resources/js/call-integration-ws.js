window.com_haulmont_thesis_crm_phoneintegration_web_CallIntegrationJavaScriptComponent = function () {

    var connector = this;
    var connection = null;
    var timerId;

    /*readyState
    Constant 	Value 	Description
    CONNECTING 	0 	    The connection is not yet open.
    OPEN 	    1 	    The connection is open and ready to communicate.
    CLOSING 	2 	    The connection is in the process of closing.
    CLOSED  	3 	    The connection is closed or couldn't be opened.
    */

    this.invokeCommand = function() {
        sendMessage(connector.getState().command);
    };
   
    this.onStateChange = function() {
       /*
        if (!isCallIntegrationEnabled()) {
            closeConnection();
            return;
        } else {
            if (connection == null) {
                openConnection()
            }
        };
        */
        sendResultToServer('onStateChange');
    };
    
    this.load = function(address) {
        if (connection != null) return true;
        sendResultToServer('Softphone connection opening by address "{0}"...'.f(address));
        try  {
            connection = new WebSocket(address);

            connection.onopen = function(event){
                //connector.getState().isConnected = true;
                //timerId = setTimeout(function tick() {
                //    connector.setResult('heartbeat');
                //    timerId = setTimeout(tick, 300000);
                //}, 300000);

                console.log ('{0}: Softphone connection open. Address: {1}.'.f( getTime(), address));
                connector.wsConnected();
            };

            connection.onclose = function(event){
                clearTimeout(timerId);
                var reason;
                //connector.getState().isConnected = false;
                console.log ('{0}: Softphone connection close. Code: {1}'.f( getTime(), event.code));
                // See http://tools.ietf.org/html/rfc6455#section-7.4.1
                switch (event.code){
                    case 1000:
                        reason = 'Normal closure, meaning that the purpose for which the connection was established has been fulfilled.';
                        break;
                    case 1001:
                        reason = 'An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.';
                        break;
                    case 1002:
                        reason = 'An endpoint is terminating the connection due to a protocol error';
                        break;
                    case 1003:
                        reason = 'An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).';
                        break;
                    case 1004:
                        reason = 'Reserved. The specific meaning might be defined in the future.';
                        break;
                    case 1005:
                        reason = 'No status code was actually present.';
                        break;
                    case 1006:
                        reason = 'The connection was closed abnormally, e.g., without sending or receiving a Close control frame';
                        break;
                    case 1007:
                        reason = 'An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [http://tools.ietf.org/html/rfc3629] data within a text message).';
                        break;
                    case 1008:
                        reason = 'An endpoint is terminating the connection because it has received a message that \"violates its policy\". This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.';
                        break;
                    case 1009:
                        reason = 'An endpoint is terminating the connection because it has received a message that is too big for it to process.';
                        break;
                    case 1010: // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
                        reason = 'An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn\'t return them in the response message of the WebSocket handshake.Specifically, the extensions that are needed are: ' + event.reason;
                        break;
                    case 1011:
                        reason = 'A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.';
                        break;
                    case 1015:
                        reason = 'The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can\'t be verified).';
                        break;
                    default:
                        reason = 'Unknown reason';
                }
               connector.wsDisconnected();
            };

            connection.onmessage = receiveMessage;

            connection.onerror = function (event) {
                console.log ('{0}: Softphone connection error.'.f( getTime()));
                connector.error();
            };
        }
        catch(er) {
            //sendResultToServer(er);
            console.log(er);
        }
    };
    
    this.unload = function() {
        if (connection != null) {
            console.log('Softphone connection closing...');
            connection.close;
            connection = null;
        }
    };

    this.getConnectionState = function() {
        if (connection != null) {
            return connection.readyState.toString();
        }
    };

    function sendResultToServer(value) {
        connector.setResult(value);
    };

    function sendMessage(command) {
        if (connection == null) {
            sendResultToServer('cant send message, connection close');
        } else { 
            sendResultToServer('Invoke Command: {0}; param1: {1}; param2: {2}.'.f(command.title, command.param1, command.param2));
            connection.send(JSON.stringify(command));
        }
    };
        
    function receiveMessage(e) {
        var serviceMessage = JSON.parse(e.data);
        //sendResultToServer('ServiceMessage: {0}> {1}'.f(serviceMessage.version, serviceMessage.title));
        
        switch (serviceMessage.title ){
            case 'CallStatusChanged':
                switch (serviceMessage.state){
                    case 1: //Ringing
                        if (serviceMessage.isIncoming){
                            connector.incomingCall(serviceMessage);
                        }
                        sendResultToServer('{0}. Ringing. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));         
                        break;
                    case 2: //Dialing
                         if (!serviceMessage.isIncoming){
                            connector.outgoingCall(serviceMessage);
                        }
                        sendResultToServer('{0}.Dialing. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                  
                        break;
                    case 3: // Connected
                        connector.callEstablished(serviceMessage);
                        sendResultToServer('{0}.Connected. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                  
                        break;
                    case 4: // WaitingForNewParty
                        connector.waitingForNewParty(serviceMessage); 
                        sendResultToServer('{0}.WaitingForNewParty. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                           
                        break;
                    case 5: // TryingToTransfer
                        connector.tryingToTransfer(serviceMessage);
                        sendResultToServer('{0}.TryingToTransfer. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                              
                        break;
                    case 6: //CallEnded
                        connector.callEnded(serviceMessage);
                        sendResultToServer('{0}.CallEnded. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                  
                        break;
                    default:  //Undefined
                    sendResultToServer('{0}.Undefined. CallId: {1}, State: {2}, OtherPartyNumber: {3}'.f(serviceMessage.title, serviceMessage.callId, serviceMessage.state, serviceMessage.otherPartyNumber));                                      
                }
                break;
            case 'CurrentProfileChanged':
                connector.setActiveProfile(serviceMessage.profile)
                sendResultToServer('ServiceMessage: {0}, Profile: {1}'.f(serviceMessage.title, serviceMessage.profile));
                break;
            case 'MakeCall':
                sendResultToServer('Service Message: {0}, param1: {1}'.f(serviceMessage.title, serviceMessage.param1));
                break;
            case 'exitFromQueues':
                connector.exitFromQueues()
                sendResultToServer('Service Message: {0}'.f(serviceMessage.title));
                break;
            default:
                sendResultToServer('Service Message: {0}'.f(serviceMessage.title)); 
        }
    };

    function getTime() {
        var date = new Date();
        var options = { year: "numeric", month: "numeric", day: "numeric",
                        hour: "2-digit", minute: "2-digit"
                      };
       
        return date.toLocaleTimeString("ru-ru", options);
    }; 
    
    String.prototype.format = String.prototype.f = function () {
        var args = arguments;
        return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function (m, n) {
            if (m == "{{") { return "{"; }
            if (m == "}}") { return "}"; }
            return args[n];
        });
    };

};