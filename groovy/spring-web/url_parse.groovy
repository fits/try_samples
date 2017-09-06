@Grab('org.springframework:spring-web:4.3.10.RELEASE')
import org.springframework.web.util.UriComponentsBuilder

def uri = 'http://localhost:8080/data?n=sample1&v=123'

def comp = UriComponentsBuilder.fromUriString(uri).build()

println comp.scheme
println comp.host
println comp.port
println comp.path
println comp.fragment
println comp.query
println comp.queryParams
