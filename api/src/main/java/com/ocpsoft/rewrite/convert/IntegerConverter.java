/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.rewrite.convert;

import com.ocpsoft.rewrite.bind.Converter;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class IntegerConverter implements Converter<Integer>
{
   @Override
   public Integer convert(final Rewrite event, final EvaluationContext context, final Object value)
   {
      if (value == null)
      {
         throw new IllegalArgumentException("Value to convert cannot be null.");
      }
      else if (value.getClass().isArray())
      {
         if (((Object[]) value).length > 0)
         {
            return Integer.valueOf(((Object[]) value)[0].toString());
         }
         else
         {
            throw new IllegalArgumentException("Value to convert was empty array.");
         }
      }
      return Integer.valueOf(value.toString());
   }

}
