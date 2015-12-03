
#import <Foundation/Foundation.h>
#import "Sample1.h"

@implementation Sample1 

- (void)sample1:(NSString*)msg with:(int)value {
	NSLog(@"message = %@, %d", msg, value);
}

@end

int main(int argc, const char * argv[]) {
	@autoreleasepool {
		id<ProtSample> s = [[Sample1 new] autorelease];

		[s sample1:@"test data" with:10];
	}

	return 0;
}
