
#import <Foundation/NSObject.h>
#import <Foundation/NSString.h>

@interface Sample : NSObject {
	NSString* _name;
}

@property (retain) NSString* name;

- (void)log;

@end
