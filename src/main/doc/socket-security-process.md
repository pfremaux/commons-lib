# Security process for sockets
This library allows you to create basic client/server communication. Nowadays we always seek for encryption, security.

:alien:
## Classes

### commons.lib.server.socket.Message
This abstract class allows you to describe a message the client or server would send.
You have to set 3 mandatory values :
* String responseHostname : IP or hostname of the caller,
* int responsePort : listening port of the caller,
* boolean requireResponse : you must set this value to true if you expect to get a response. If the consumer of the receiver generated a result with your call, you won't get it unless you set it to true.


### commons.lib.server.socket.Wrapper
You need this class to store the message you want to send with its action code : int action
You should have to bother about the other attributes and methods they are used internally.


### commons.lib.server.socket.MessageConsumer
This interface

Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort);


