//
//  StringCustomPicker.h
//  Cleanium
//
//  Created by  DN-117 on 8/25/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ActionSheetCustomPickerDelegate.h"

@protocol StringPickerDelegate
-(void)stringPickerDone:(NSString*)selectedAddress withIndex:(NSInteger)index withType:(NSString*)type;
@optional
-(void)stringPickerCancel;
@end

@interface StringCustomPicker : NSObject<ActionSheetCustomPickerDelegate>

@property (nonatomic, weak) id<StringPickerDelegate> delegate;
@property (nonatomic, strong) NSMutableArray *stringArray;
@property (nonatomic, retain) NSString *selectedString;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, retain) NSString *type;

@end
