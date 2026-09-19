import { createClient } from '@supabase/supabase-js';
import bcrypt from 'bcryptjs';

export const SUPABASE_URL = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_SUPABASE_URL)
  || localStorage.getItem('chewchecker_supabase_url')
  || 'https://zktnnwlkkmamzctuzano.supabase.co';

export const SUPABASE_ANON_KEY = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_SUPABASE_ANON_KEY)
  || localStorage.getItem('chewchecker_supabase_anon_key')
  || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdG5ud2xra21hbXpjdHV6YW5vIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk3ODU5NDEsImV4cCI6MjEwNTM2MTk0MX0.iETlSKThgYvfNNIxTurOk8FlcC8h6d1RdwEWLY9devg';

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

/**
 * Direct Login against Supabase `users` table
 */
export async function supabaseLogin(email, password) {
  try {
    const { data, error } = await supabase
      .from('users')
      .select('*')
      .ilike('email', email.trim())
      .single();

    if (error || !data) {
      return { success: false, message: 'Invalid email or user not found' };
    }

    if (!data.is_active) {
      return { success: false, message: 'Account is deactivated' };
    }

    // Verify bcrypt hash or plain fallback
    let match = false;
    try {
      if (data.password_hash && data.password_hash.startsWith('$2')) {
        match = bcrypt.compareSync(password, data.password_hash);
      }
    } catch (e) {
      match = false;
    }

    if (!match && data.password_hash === password) {
      match = true;
    }

    if (!match) {
      return { success: false, message: 'Incorrect password' };
    }

    return {
      success: true,
      user: {
        id: data.id,
        name: data.name,
        email: data.email,
        role: data.role.toUpperCase(),
        mobileNumber: data.mobile_number,
        address: data.address,
        profilePicturePath: data.profile_picture_path
      },
      token: 'sb_' + btoa(data.id + ':' + data.email)
    };
  } catch (err) {
    console.error('Supabase login error:', err);
    return { success: false, message: err.message || 'Login connection failed' };
  }
}

/**
 * Direct Registration in Supabase `users` table
 */
export async function supabaseRegister(name, email, password, role = 'PARENT') {
  try {
    // Check if user already exists
    const { data: existing } = await supabase
      .from('users')
      .select('id')
      .ilike('email', email.trim())
      .maybeSingle();

    if (existing) {
      return { success: false, message: 'An account with this email already exists' };
    }

    const salt = bcrypt.genSaltSync(10);
    const passwordHash = bcrypt.hashSync(password, salt);

    const { data, error } = await supabase
      .from('users')
      .insert([
        {
          name: name.trim(),
          email: email.trim().toLowerCase(),
          password_hash: passwordHash,
          role: role.toUpperCase(),
          is_active: true
        }
      ])
      .select()
      .single();

    if (error || !data) {
      return { success: false, message: error?.message || 'Failed to create user account' };
    }

    return {
      success: true,
      user: {
        id: data.id,
        name: data.name,
        email: data.email,
        role: data.role.toUpperCase()
      },
      token: 'sb_' + btoa(data.id + ':' + data.email)
    };
  } catch (err) {
    console.error('Supabase register error:', err);
    return { success: false, message: err.message || 'Registration failed' };
  }
}

/**
 * Update Profile in Supabase
 */
export async function supabaseUpdateProfile(userId, profileData) {
  try {
    const updatePayload = {
      name: profileData.name,
      email: profileData.email,
      mobile_number: profileData.mobileNumber,
      address: profileData.address
    };

    if (profileData.password) {
      const salt = bcrypt.genSaltSync(10);
      updatePayload.password_hash = bcrypt.hashSync(profileData.password, salt);
    }

    const { data, error } = await supabase
      .from('users')
      .update(updatePayload)
      .eq('id', userId)
      .select()
      .single();

    if (error) throw error;
    return { success: true, user: data };
  } catch (err) {
    console.error('Supabase update profile error:', err);
    return { success: false, message: err.message };
  }
}

/**
 * Fetch Children for Parent
 */
export async function supabaseGetParentChildren(parentId) {
  try {
    const { data, error } = await supabase
      .from('parent_student')
      .select(`
        relationship,
        is_primary,
        student:students (
          id,
          name,
          student_code,
          gender,
          date_of_birth,
          blood_group,
          height_cm,
          weight_kg,
          roll_number,
          school_id,
          class_id,
          box_length,
          box_width,
          box_depth,
          box_volume,
          box_shape,
          box_compartments,
          daily_calories,
          daily_protein,
          daily_carbs,
          daily_fat,
          daily_fiber,
          lunch_calories,
          lunch_protein,
          lunch_carbs,
          lunch_fat,
          lunch_fiber,
          class:classes (
            id,
            name,
            grade,
            section,
            join_code
          ),
          school:schools (
            id,
            name
          )
        )
      `)
      .eq('parent_id', parentId);

    if (error) throw error;

    return (data || []).map(row => {
      const s = row.student || {};
      const c = s.class || {};
      const sc = s.school || {};
      return {
        id: s.id,
        studentId: s.id,
        name: s.name,
        studentCode: s.student_code,
        gender: s.gender,
        dateOfBirth: s.date_of_birth,
        bloodGroup: s.blood_group,
        heightCm: s.height_cm,
        weightKg: s.weight_kg,
        rollNumber: s.roll_number,
        className: c.name ? `${c.grade} - ${c.section} (${c.name})` : (c.grade || 'Grade 1'),
        classId: c.id,
        schoolName: sc.name || 'Primary School',
        schoolId: sc.id,
        relationship: row.relationship,
        lunchboxLength: s.box_length,
        lunchboxWidth: s.box_width,
        lunchboxDepth: s.box_depth,
        lunchboxCapacity: s.box_volume,
        lunchboxShape: s.box_shape,
        lunchboxCompartments: s.box_compartments,
        dailyTarget: {
          calories: s.daily_calories || 1500,
          protein: s.daily_protein || 45,
          carbs: s.daily_carbs || 190,
          fat: s.daily_fat || 50,
          fiber: s.daily_fiber || 22
        },
        lunchTarget: {
          calories: s.lunch_calories || 500,
          protein: s.lunch_protein || 18,
          carbs: s.lunch_carbs || 65,
          fat: s.lunch_fat || 16,
          fiber: s.lunch_fiber || 8
        }
      };
    });
  } catch (err) {
    console.error('Supabase fetch parent children error:', err);
    return [];
  }
}

/**
 * Fetch Lunchbox Presets for Student
 */
export async function supabaseGetPresetsForStudent(studentId) {
  try {
    const { data, error } = await supabase
      .from('lunchbox_presets')
      .select('*')
      .eq('student_id', studentId)
      .order('created_at', { ascending: false });

    if (error) throw error;
    return (data || []).map(p => ({
      id: p.id,
      presetName: p.preset_name,
      brand: p.brand,
      studentId: p.student_id,
      userId: p.user_id,
      lengthCm: p.length_cm,
      widthCm: p.width_cm,
      depthCm: p.depth_cm,
      capacityMl: p.capacity_ml,
      compartments: p.compartments,
      shape: p.shape,
      material: p.material,
      isDefault: p.is_default
    }));
  } catch (err) {
    console.error('Supabase fetch presets error:', err);
    return [];
  }
}

/**
 * Save / Create Lunchbox Preset
 */
export async function supabaseSavePreset(preset) {
  try {
    const payload = {
      preset_name: preset.presetName,
      brand: preset.brand || 'Custom',
      student_id: preset.studentId,
      user_id: preset.userId,
      length_cm: preset.lengthCm,
      width_cm: preset.widthCm,
      depth_cm: preset.depthCm,
      capacity_ml: preset.capacityMl,
      compartments: preset.compartments || 1,
      shape: preset.shape || 'RECTANGULAR',
      material: preset.material || 'Plastic',
      is_default: preset.isDefault || false
    };

    const { data, error } = await supabase
      .from('lunchbox_presets')
      .insert([payload])
      .select()
      .single();

    if (error) throw error;
    return {
      id: data.id,
      presetName: data.preset_name,
      brand: data.brand,
      studentId: data.student_id,
      userId: data.user_id,
      lengthCm: data.length_cm,
      widthCm: data.width_cm,
      depthCm: data.depth_cm,
      capacityMl: data.capacity_ml,
      compartments: data.compartments,
      shape: data.shape,
      material: data.material,
      isDefault: data.is_default
    };
  } catch (err) {
    console.error('Supabase save preset error:', err);
    throw err;
  }
}

/**
 * Fetch Meals for Student
 */
export async function supabaseGetMealsForStudent(studentId) {
  try {
    const { data, error } = await supabase
      .from('meals')
      .select(`
        *,
        meal_food_items (*),
        nutrition_scores (*)
      `)
      .eq('student_id', studentId)
      .order('meal_date', { ascending: false });

    if (error) throw error;

    return (data || []).map(m => {
      const items = (m.meal_food_items || []).map(it => ({
        id: it.id,
        foodName: it.food_name,
        quantity: it.quantity,
        calories: it.calories,
        proteinG: it.protein_g,
        carbsG: it.carbs_g,
        fatG: it.fat_g,
        fiberG: it.fiber_g,
        leftoverPercentage: it.leftover_percentage || 0
      }));

      const scoreObj = (m.nutrition_scores && m.nutrition_scores[0]) || {};

      return {
        id: m.id,
        studentId: m.student_id,
        mealDate: m.meal_date,
        status: m.status,
        preMealImageUrl: m.pre_meal_image_url,
        postMealImageUrl: m.post_meal_image_url,
        boxLength: m.box_length,
        boxWidth: m.box_width,
        boxHeight: m.box_height,
        lunchboxPresetId: m.lunchbox_preset_id,
        lunchboxPresetName: m.lunchbox_preset_name,
        foodItems: items,
        nutritionScore: scoreObj.score || 0,
        classification: scoreObj.classification || 'GOOD',
        totalConsumedCalories: scoreObj.total_consumed_calories || 0,
        totalConsumedProteinG: scoreObj.total_consumed_protein_g || 0,
        totalConsumedCarbsG: scoreObj.total_consumed_carbs_g || 0,
        totalConsumedFatG: scoreObj.total_consumed_fat_g || 0,
        totalConsumedFiberG: scoreObj.total_consumed_fiber_g || 0
      };
    });
  } catch (err) {
    console.error('Supabase fetch meals error:', err);
    return [];
  }
}

/**
 * Save Meal (Pre-Meal or Leftover)
 */
export async function supabaseSaveMeal(mealData) {
  try {
    const studentId = mealData.studentId;
    const mealDate = mealData.mealDate || new Date().toISOString().split('T')[0];

    // Check if meal row already exists for student & date
    const { data: existingMeal } = await supabase
      .from('meals')
      .select('id')
      .eq('student_id', studentId)
      .eq('meal_date', mealDate)
      .maybeSingle();

    let mealRecord = null;
    if (existingMeal) {
      const updateObj = {};
      if (mealData.status) updateObj.status = mealData.status;
      if (mealData.postMealImageUrl) updateObj.post_meal_image_url = mealData.postMealImageUrl;
      if (mealData.preMealImageUrl) updateObj.pre_meal_image_url = mealData.preMealImageUrl;
      if (mealData.uploadedByParent) updateObj.uploaded_by_parent = mealData.uploadedByParent;
      if (mealData.uploadedByTeacher) updateObj.uploaded_by_teacher = mealData.uploadedByTeacher;

      const { data, error } = await supabase
        .from('meals')
        .update(updateObj)
        .eq('id', existingMeal.id)
        .select()
        .single();
      if (error) throw error;
      mealRecord = data;
    } else {
      const insertObj = {
        student_id: studentId,
        meal_date: mealDate,
        status: mealData.status || 'PRE_MEAL_UPLOADED',
        pre_meal_image_url: mealData.preMealImageUrl || null,
        post_meal_image_url: mealData.postMealImageUrl || null,
        uploaded_by_parent: mealData.uploadedByParent || null,
        uploaded_by_teacher: mealData.uploadedByTeacher || null,
        box_length: mealData.boxLength || 20,
        box_width: mealData.boxWidth || 15,
        box_height: mealData.boxHeight || 6,
        lunchbox_preset_id: mealData.lunchboxPresetId || null,
        lunchbox_preset_name: mealData.lunchboxPresetName || null
      };

      const { data, error } = await supabase
        .from('meals')
        .insert([insertObj])
        .select()
        .single();
      if (error) throw error;
      mealRecord = data;
    }

    // Insert or update food items if provided
    if (Array.isArray(mealData.foodItems) && mealData.foodItems.length > 0) {
      await supabase.from('meal_food_items').delete().eq('meal_id', mealRecord.id);

      const itemsToInsert = mealData.foodItems.map(item => ({
        meal_id: mealRecord.id,
        food_name: item.foodName || 'Home-packed meal',
        quantity: item.quantity || '1 portion',
        calories: item.calories || 300,
        protein_g: item.proteinG || 10,
        carbs_g: item.carbsG || 40,
        fat_g: item.fatG || 8,
        fiber_g: item.fiberG || 4,
        leftover_percentage: item.leftoverPercentage || 0,
        confidence_score: 0.95
      }));

      await supabase.from('meal_food_items').insert(itemsToInsert);
    }

    return mealRecord;
  } catch (err) {
    console.error('Supabase save meal error:', err);
    throw err;
  }
}

/**
 * Fetch Teacher Classes & Students
 */
export async function supabaseGetTeacherClasses(teacherId) {
  try {
    const { data: classes, error } = await supabase
      .from('classes')
      .select(`
        id,
        name,
        grade,
        section,
        join_code,
        school:schools (id, name),
        students (
          id,
          name,
          student_code,
          gender,
          roll_number,
          date_of_birth,
          blood_group,
          daily_calories,
          daily_protein,
          lunch_calories,
          lunch_protein
        )
      `)
      .eq('teacher_id', teacherId);

    if (error) throw error;

    return (classes || []).map(c => ({
      id: c.id,
      name: c.name,
      grade: c.grade,
      section: c.section,
      joinCode: c.join_code,
      schoolName: c.school?.name || 'School',
      students: (c.students || []).map(s => ({
        id: s.id,
        studentId: s.id,
        name: s.name,
        studentCode: s.student_code,
        gender: s.gender,
        rollNumber: s.roll_number,
        dateOfBirth: s.date_of_birth,
        bloodGroup: s.blood_group,
        dailyTarget: {
          calories: s.daily_calories || 1500,
          protein: s.daily_protein || 45
        },
        lunchTarget: {
          calories: s.lunch_calories || 500,
          protein: s.lunch_protein || 18
        }
      }))
    }));
  } catch (err) {
    console.error('Supabase get teacher classes error:', err);
    return [];
  }
}

/**
 * Fetch All Users for Admin
 */
export async function supabaseGetUsers() {
  try {
    const { data, error } = await supabase
      .from('users')
      .select('*')
      .order('id', { ascending: true });

    if (error) throw error;
    return (data || []).map(u => ({
      id: u.id,
      name: u.name,
      email: u.email,
      role: u.role,
      status: u.is_active ? 'Active' : 'Inactive',
      details: u.role === 'ADMIN' ? 'System Administrator' : (u.role === 'TEACHER' ? 'Class Teacher' : 'Parent Account')
    }));
  } catch (err) {
    console.error('Supabase fetch users error:', err);
    return [];
  }
}

/**
 * Fetch Holidays
 */
export async function supabaseGetHolidays() {
  try {
    const { data, error } = await supabase
      .from('holidays')
      .select('*')
      .order('date', { ascending: true });

    if (error) throw error;
    return data || [];
  } catch (err) {
    console.error('Supabase fetch holidays error:', err);
    return [];
  }
}

/**
 * Messaging: Fetch Conversation
 */
export async function supabaseGetMessages(userId1, userId2) {
  try {
    const { data, error } = await supabase
      .from('messages')
      .select('*')
      .or(`and(sender_id.eq.${userId1},receiver_id.eq.${userId2}),and(sender_id.eq.${userId2},receiver_id.eq.${userId1})`)
      .order('sent_at', { ascending: true });

    if (error) throw error;
    return (data || []).map(m => ({
      id: m.id,
      senderId: m.sender_id,
      receiverId: m.receiver_id,
      messageText: m.message_text,
      sentAt: m.sent_at
    }));
  } catch (err) {
    console.error('Supabase get messages error:', err);
    return [];
  }
}

/**
 * Messaging: Send Message
 */
export async function supabaseSendMessage(senderId, receiverId, messageText) {
  try {
    const { data, error } = await supabase
      .from('messages')
      .insert([
        {
          sender_id: senderId,
          receiver_id: receiverId,
          message_text: messageText
        }
      ])
      .select()
      .single();

    if (error) throw error;
    return data;
  } catch (err) {
    console.error('Supabase send message error:', err);
    throw err;
  }
}
