
import java.time.*

def d = OffsetDateTime.now()

println d

def nextHour = d.plusHours(1).withMinute(0).withSecond(0).withNano(0)

println nextHour

// java.util.Date への変換
println Date.from(nextHour.toInstant())
