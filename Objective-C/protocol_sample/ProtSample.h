
#import <Foundation/NSString.h>

@protocol ProtSample

- (void)sample1:(NSString*)msg with:(int)value;

@optional
- (int)sample2:(int)value;

@end
