# Security process for sockets
This library allows you to create basic client/server communication. Nowadays we always seek for encryption, security.

## Classes

### commons.lib.server.socket.Message
This abstract class allows you to describe a message the client or server would send.
You have to set 3 mandatory values :
* String responseHostname : IP or hostname of the caller,
* int responsePort : listening port of the caller,
* boolean requireResponse : you must set this value to true if you expect to get a response. If the consumer of the receiver generated a result with your call, you won't get it unless you set it to true.
By convention, we're declaring in this class a static attribute with a unique value of int.
It will help the server to identify the type of this message. 
You'll have to implements the serialize() method. It has to convert the Message instance to byte[]. 

### commons.lib.server.socket.Wrapper
You need this class to store the message you want to send with its action code : int action
You don't have to bother about the other attributes and methods they are used internally.


### commons.lib.server.socket.MessageConsumer
This interface allows you to process a message. You have to provide to its unique method the 
message the client sent to you and the receiver's hostname and listening port.

Optional<Wrapper> process(Wrapper input, String consumerHostname, int consumerPort);

Regarding the situation you might want to prepare a response and return it as a Wrapper.
Otherwise, just return an empty Optional.

### commons.lib.server.socket.MessageConsumerManager
This class needs to be initialize before any usage of sockets. You have to register all
your MessageConsumers.

final MessageConsumerManager messageConsumerManager = new MessageConsumerManager();
messageConsumerManager.register(MyMessage.CODE, new MyMessageConsumer(parameter));


### commons.lib.server.socket.WrapperFactory
You have to register all your wrapper generator in WrapperFactory.
In other words, once a message is deserialized, you'll get a List<String>. 
You have to implement a converter that will instantiate a message within a wrapper. 

final Map<Integer, Function<List<String>, Wrapper>> wrappers = new HashMap<>();
wrappers.put(MyMessage.CODE, strings -> new Wrapper(MyMessage.CODE, new MyMessage(strings)));
final WrapperFactory wrapperFactory = new WrapperFactory(wrappers); 


## Initialization of an encrypted communication

GetServerPublicKeysMessage : contains a symmetric key the receiver will use to encrypt the response. The second datum is the number of publickeys needed.
GetServerPublicKeysMessageConsumer : will generate the key pairs, store them locally and 
prepare the response with the public keys encrypted with the symmetric key.

EncryptedPublicKeysMessage : Contains public keys
SymEncryptedPublicKeysMessageConsumer : will store public keys needed to communicate with the host and prepare the response with the same number of public keys. Thanks to it the host will be able to 
respond with the same encrypted solution.

EncryptedPublicKeysMessage : already described
AsymEncryptedPublicKeysMessageConsumer : will store public keys needed to communicate with the caller
                                         and register the caller as a valid encrypted target. Prepare the ACK
                                     
AckEncryptedExchangeMessage : contains a TTL
AckEncryptedExchangeMessageConsumer : acknowledge and register the host as a valid encrypted target.
