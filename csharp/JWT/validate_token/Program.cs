using System.IdentityModel.Tokens.Jwt;
using Microsoft.IdentityModel.Tokens;

var token = args[0];

var opts = new TokenValidationParameters
{
    SignatureValidator = (string t, TokenValidationParameters p) => new JwtSecurityToken(t),
    ValidateAudience = false,
    ValidateIssuer = false,
    ValidateLifetime = false,
};

var handler = new JwtSecurityTokenHandler();

handler.ValidateToken(token, opts, out SecurityToken validatedToken);

Console.WriteLine($"valid token = {validatedToken}");
