@Grab('com.fasterxml.jackson.core:jackson-databind:2.8.7')
@Grab('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.8.7')
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import groovy.transform.Canonical
import java.time.*

@Canonical
class Sample {
	LocalDateTime d1
	OffsetDateTime d2
	ZonedDateTime d3
	Date d4
}

def test = { mapper -> 

	def d = new Sample(
		LocalDateTime.now(), 
		OffsetDateTime.now(), 
		ZonedDateTime.now(), 
		new Date()
	)

	def json = mapper.writeValueAsString(d)

	println "ser: ${json}"

	try {
		println "des: ${mapper.readValue(json, Sample.class)}"
	} catch(e) {
		println "ERROR: ${e}"
	}

	println ''
}

println '----- (1) normal -----'

test(new ObjectMapper())

println '----- (2) JavaTimeModule -----'

def m2 = new ObjectMapper()
m2.registerModule(new JavaTimeModule())

test(m2)

println '----- (3) disable: WRITE_DATES_AS_TIMESTAMPS -----'

def m3 = new ObjectMapper()
m3.registerModule(new JavaTimeModule())

m3.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

test(m3)

println '----- (4) disable: WRITE_DATES_AS_TIMESTAMPS, ADJUST_DATES_TO_CONTEXT_TIME_ZONE -----'

def m4 = new ObjectMapper()
m4.registerModule(new JavaTimeModule())

m4.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
m4.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

test(m4)
