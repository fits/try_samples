@Grab('org.apache.commons:commons-lang3:3.1')
import org.apache.commons.lang3.RandomStringUtils

println RandomStringUtils.randomAlphanumeric(15)
println RandomStringUtils.randomAscii(15)

// w’è‚Ì•¶š‚ğg‚Á‚½ƒ‰ƒ“ƒ_ƒ€•¶š—ñì¬
println RandomStringUtils.random(15, '123456789-abcdef')
println RandomStringUtils.random(15, '123456789-abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ')
