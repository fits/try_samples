#import <Foundation/Foundation.h>
#import <Foundation/NSObject.h>

@interface Cls : NSObject {
	int v;
}

- (id)initWithValue:(int)value;
- (void)sample;

@end

@implementation Cls : NSObject

- (id)initWithValue:(int)value {
	self = [super init];

	if (self != nil) {
		self->v = value;
	}

	return self;
}

- (void)sample {
	NSLog(@"%d: call sample", v);
}

- (void)dealloc {
	NSLog(@"%d: *** Cls dealloc", v);
	[super dealloc];
}

@end

id createCls1() {
	return [[Cls alloc] initWithValue:1];
}

void test1() {
	id s = createCls1();
	[s sample];
}

id createCls2() {
	return [[[Cls alloc] initWithValue:2] autorelease];
}

void test2() {
	@autoreleasepool {
		id s = createCls2();
		[s sample];
	}
}

int main(int argc, const char * argv[]) {
	// not dealloc
	test1();

	// dealloc
	test2();

	return 0;
}
