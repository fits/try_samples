
#import <Foundation/Foundation.h>
#import "Sample.h"

@implementation Sample : NSObject

@synthesize name = _name;

- (void)log {
    NSLog(@"%@", _name);
}

- (void)dealloc {
    [_name release];
    [super dealloc];
}

@end

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        Sample* s = [[Sample new] autorelease];

        s.name = @"test";

        [s log];
    }

    return 0;
}
