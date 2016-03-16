//
//  LocationCustomPicker.h
//  Cleanium
//
//  Created by  DN-117 on 7/29/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ActionSheetCustomPickerDelegate.h"
#import <Parse/Parse.h>

@protocol LocactionPickerDelegate
-(void)locationPickerDone:(PFObject*)selectedAddress withIndex:(NSInteger)index;
@end

@interface LocationCustomPicker : NSObject <ActionSheetCustomPickerDelegate>

{
    NSArray *addresses;
    PFObject *selectedAddress;
    NSInteger selectedIndex;
}

@property (nonatomic, weak) id<LocactionPickerDelegate> delegate;

@end
