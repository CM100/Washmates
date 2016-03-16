//
//  TimeCustomPicker.h
//  Cleanium
//
//  Created by  DN-117 on 8/3/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ActionSheetCustomPickerDelegate.h"
#import "AppDelegate.h"

@protocol TimePickerDelegate
-(void)timePickerDone:(NSDate*)selectedTime witTimeIndex:(NSInteger)timeIndex withType:(NSString*)type;
@end

@interface TimeCustomPicker : NSObject<ActionSheetCustomPickerDelegate>{
    
    NSArray *timeArray;
    
    AppDelegate *appDelegate;
    NSDateFormatter *df;
}

@property (nonatomic, weak) id<TimePickerDelegate> delegate;

@property (nonatomic, retain) NSString *type;
@property (nonatomic, assign) NSInteger selectedTimeIndex;
@property (nonatomic, retain) NSString *selectedTime;
@property (nonatomic, retain) NSDate *selectedTimeDate;

-(void)setTimeArrays:(NSArray *)timeArrayLocal;

@end
