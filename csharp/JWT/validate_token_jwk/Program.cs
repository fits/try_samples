using System.IdentityModel.Tokens.Jwt;
using Microsoft.IdentityModel.Tokens;

var jwkFile = args[0];
var token = args[1];

var jwk = File.ReadAllText(jwkFile);

var opts = new TokenValidationParameters
{
    ValidateAudience = false,
    ValidateIssuer = false,
    ValidateLifetime = false,
    IssuerSigningKey = new JsonWebKey(jwk)
};

var handler = new JwtSecurityTokenHandler();

handler.ValidateToken(token, opts, out SecurityToken validatedToken);

Console.WriteLine($"valid token = {validatedToken}");
