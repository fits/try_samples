
const AWS = require('aws-sdk');

AWS.config.loadFromPath('./config.json');

const ec2 = new AWS.EC2();

const params = {
	Owners: ['amazon'],
	Filters: [
		{ Name: 'name', Values: ['*-amazon-ecs-optimized'] }
	]
};

const compareDate = (a, b) => (a.CreationDate < b.CreationDate) ? 1 : -1;

ec2.describeImages(params).promise()
	.then(res => res.Images.sort(compareDate))
	.then(imgs => imgs[0])
	.then(img => console.log(`id: ${img.ImageId}, name: ${img.Name}, date: ${img.CreationDate}, description: ${img.Description}`))
	.catch(console.error);
