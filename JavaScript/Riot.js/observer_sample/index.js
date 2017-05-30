require('./counter.tag');
require('./listener.tag');

const observer = riot.observable();

riot.mount('*', { observer: observer });
