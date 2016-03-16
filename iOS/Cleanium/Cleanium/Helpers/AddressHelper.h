//
//  AddressHelper.h
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Parse/Parse.h>
#import "SignUp2ViewController.h"

@interface AddressHelper : NSObject

+(void)performInsertAddress:(SignUp2ViewController*)viewController withAddress:(PFObject*)addressObject isUpdate:(bool)isUpdate;
+(void)performDeleteAddress:(SignUp2ViewController*)viewController withAddress:(PFObject*)addressObject;

@end
