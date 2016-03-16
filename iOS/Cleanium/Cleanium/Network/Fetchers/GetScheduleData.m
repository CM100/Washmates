//
//  GetScheduleData.m
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "GetScheduleData.h"
#import <Parse/Parse.h>
#import "AppDelegate.h"
#import "ScheduleData.h"

@implementation GetScheduleData

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    NSMutableArray *postCodes = [[NSMutableArray alloc] init];
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    for (int i = 0; i < appDelegate.userAddresses.count; i++) {
        PFObject *address = [appDelegate.userAddresses objectAtIndex:i];
        NSString *postCode = [address objectForKey:@"postCode"];
        if(postCode){
            [postCodes addObject:postCode];
        }
    }
    
    [PFCloud callFunctionInBackground:@"getSchedulesForPostCodes"
                       withParameters:@{@"postCodes": postCodes}
                                block:^(NSDictionary *response, NSError *error) {
                                    if (error) {
                                        UIAlertView *alertView =
                                        [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                   message:error.localizedDescription
                                                                  delegate:nil
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"Ok", nil];
                                        [alertView show];
                                        completionBlock(nil, error);
                                        return;
                                    }else {
                                        NSLog(@"response %@", response);
                                        
                                        NSError *resultError = nil;
                                        id object = [self parseScheduleDataResult:response error:&resultError];
                                        completionBlock(object ,resultError);
                                    }
                                }];
    
}

- (id)parseScheduleDataResult:(NSDictionary *)rootObject error:(NSError **)error{
    
    if (![rootObject isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    
    ScheduleData *scheduleData = [[ScheduleData alloc] initWithJson:rootObject];
    
    return scheduleData;
}

@end
