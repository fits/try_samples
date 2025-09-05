using System.IdentityModel.Tokens.Jwt;
using System.Security.Cryptography;
using Microsoft.IdentityModel.Tokens;

var keyFile = args[0];
var token = args[1];

var key = File.ReadAllText(keyFile);

using var rsa = new RSACryptoServiceProvider();
rsa.ImportFromPem(key);

var opts = new TokenValidationParameters
{
    ValidateAudience = false,
    ValidateIssuer = false,
    ValidateLifetime = false,
    IssuerSigningKey = new RsaSecurityKey(rsa)
};

var handler = new JwtSecurityTokenHandler();

handler.ValidateToken(token, opts, out SecurityToken validatedToken);

Console.WriteLine($"valid token = {validatedToken}");
