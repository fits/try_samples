@Grab("commons-codec:commons-codec:1.6")
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

if (args.length < 2) {
	println "<secret key> <message>"
	return
}

def secretKey = args[0]
def message = args[1]

def key = new SecretKeySpec(secretKey.bytes, "AES")
def cipher = Cipher.getInstance "AES/ECB/PKCS5Padding"

//à√çÜâª
cipher.init Cipher.ENCRYPT_MODE, key

def res = cipher.doFinal message.bytes

def encMsg = Base64.encodeBase64String res

println encMsg

//ïúçÜâª
cipher.init Cipher.DECRYPT_MODE, key

def dec = Base64.decodeBase64 encMsg

println new String(cipher.doFinal(dec))
