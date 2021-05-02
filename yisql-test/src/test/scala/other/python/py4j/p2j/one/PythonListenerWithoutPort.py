from py4j.java_gateway import (JavaGateway, CallbackServerParameters, GatewayParameters, launch_gateway)

class PythonListenerWithoutPort(object):

    def __init__(self, gateway):
        self.gateway = gateway

    def notify(self, obj):
        print("Notified by Java")
        print(obj)
        gateway.jvm.System.out.println("Hello from python!")

        return "A Return Value"

    class Java:
        implements = ["other.python.py4j.p2j.one.ExampleListener"]


# if __name__ == "__main__":
#     # launch Java side with dynamic port and get back the port on which the
#     # server was bound to.
#     port = launch_gateway()
#     print(port)
#     # connect python side to Java side with Java dynamic port and start python
#     # callback server with a dynamic port
#     gateway = JavaGateway(
#         gateway_parameters=GatewayParameters(port=port),
#         callback_server_parameters=CallbackServerParameters(port=0))
#
#     # retrieve the port on which the python callback server was bound to.
#     python_port = gateway.get_callback_server().get_listening_port()
#     print(python_port)
#
#     # tell the Java side to connect to the python callback server with the new
#     # python port. Note that we use the java_gateway_server attribute that
#     # retrieves the GatewayServer instance.
#     gateway.java_gateway_server.resetCallbackClient(
#         gateway.java_gateway_server.getCallbackClient().getAddress(),
#         python_port)
#
#     listener = PythonListenerWithoutPort(gateway)
#     gateway.entry_point.registerListener(listener)
#     gateway.entry_point.notifyAllListeners()
#     gateway.shutdown()

#python调用java，不用启动java
if __name__ == "__main__":
    port = launch_gateway()
    gateway = JavaGateway(
        gateway_parameters=GatewayParameters(port=port),
        callback_server_parameters=CallbackServerParameters(port=0))
    random=gateway.jvm.java.util.Random()
    number1=random.nextInt(10)
    number2=random.nextInt(10)
    print(number1,number2)
    gateway.shutdown()
