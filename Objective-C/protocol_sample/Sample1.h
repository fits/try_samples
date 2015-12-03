
#import <Foundation/NSObject.h>
#import "ProtSample.h"

@interface Sample1 : NSObject<ProtSample>

- (void)sample1:(NSString*)msg with:(int)value;

@end
