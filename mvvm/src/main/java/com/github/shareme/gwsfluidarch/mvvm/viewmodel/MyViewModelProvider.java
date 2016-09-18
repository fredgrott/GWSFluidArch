/*
  Copyright 2014 Inloop, s.r.o.
  Modifications Copyright(c) 2016 Fred Grott

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.github.shareme.gwsfluidarch.mvvm.viewmodel;

/**
 * Your {@link android.app.Activity} must implement this interface if
 * any of the contained Fragments the {@link ViewModelHelper}
 * Created by fgrott on 9/17/2016.
 */
@SuppressWarnings("unused")
public interface MyViewModelProvider {

  /**
   * See {@link com.github.shareme.gwsfluidarch.mvvm.base.ViewModelBaseActivity} on how to implement.
   * @return the {@link ViewModelProvider}.
   */
  ViewModelProvider getViewModelProvider();
}