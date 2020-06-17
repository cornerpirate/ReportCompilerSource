import socket
import sys
import subprocess
import shlex

#class commonutils:
"""A simple python library to add convenience to the ReportCompiler Jython interface"""

# this issues a TCP request containing 'data' to the service defined by ip:port and returns the result
def tcprequest(ip, port, data):
    
    try:
        # Create a socket.
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(3)
        # Connect to server
        s.connect((ip, port))
        # Send data to server
        s.sendall(data) 
            
        # Receive the data
        reply = receive_all(s) 

        # Close the socket
            
        s.close()
        return reply
    except:
        e = sys.exc_info()[0]
        print "Error found: %s" % e  

# this gets all the data from the socket by looping until it closes and returns it as a string
def receive_all(sock):
    data = ""
    part = None
    while part != "":
        part = sock.recv(4096)
        data+=part
    return data
	
def osrequest(command):
    args = shlex.split(command)
    output = subprocess.Popen(command, stdout=subprocess.PIPE).communicate()
    print output