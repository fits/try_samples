@Grab('org.elasticmq:elasticmq-server_2.11:0.10.0')
import org.elasticmq.rest.sqs.SQSRestServerBuilder

def server = SQSRestServerBuilder.start()

println 'press key to quit'

System.in.read()

server.stopAndWait()
