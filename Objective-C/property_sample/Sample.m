
#import <Foundation/Foundation.h>
#import "Sample.h"

@implementation Sample : NSObject

@synthesize name = _name;

- (void)log {
	NSLog(@"%@", _name);
}

@end

int main(int argc, const char * argv[]) {
	@autoreleasepool {
		Sample *s = [[Sample new] autorelease];

		s.name = @"abc";

		[s log];
	}

	return 0;
}
