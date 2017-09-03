
const mosca = require('mosca');

const server = new mosca.Server();

server.on('ready', () => console.log('server started'));

server.on('clientConnected', client => 
	console.log(`client connected: ${client.id}`));

server.on('published', (packet) => 
	console.log(`published: ${JSON.stringify(packet)}`));

process.stdin.on('data', chunk =>
	server.close(() => process.exit(0))
);
