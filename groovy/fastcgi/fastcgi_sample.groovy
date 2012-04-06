
import com.fastcgi.*

def fcgi = new FCGIInterface()

while(fcgi.FCGIaccept() >= 0) {
	println "Content-type: text/plain\n\n"

	System.getProperties().each {k, v ->
		println "${k} = ${v}"
	}
}


