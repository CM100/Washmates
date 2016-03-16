//
//  GetAddressesFetcher.m
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "GetAddressesFetcher.h"
#import <Parse/Parse.h>

@implementation GetAddressesFetcher

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    PFQuery *query = [PFQuery queryWithClassName:@"Address"];
    [query whereKey:@"user" equalTo:[PFUser currentUser]];
    
    [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
        if (!error) {
            NSLog(@"Successfully retrieved %lu scores.", (unsigned long)objects.count);
        
            NSError *resultError = nil;
            completionBlock(objects ,resultError);
            
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:error.localizedDescription delegate:nil cancelButtonTitle:NSLocalizedString(@"Ok", nil) otherButtonTitles:nil];
            [alert show];
            completionBlock(nil, error);
            return;
        }
    }];
    
}

@end
