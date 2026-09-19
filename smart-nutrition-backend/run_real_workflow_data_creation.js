const fs = require('fs');

async function runRealWorkflowDataCreation() {
  console.log('=====================================================');
  console.log('CHEWCHECKERS – REAL WORKFLOW TEST DATA CREATION');
  console.log('=====================================================\n');

  let parentId, studentId, classId, mealId, activeClassCode;

  try {
    // PRE-STEP: LOGIN AS TEACHER TO GET ACTIVE CLASS CODE
    console.log('--- PRE-STEP: TEACHER LOGIN TO GET CLASS CODE ---');
    const tLoginResPre = await fetch('http://localhost:8088/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'jothi@gmail.com', password: 'password123' })
    });
    const tLoginDataPre = await tLoginResPre.json();
    const teacherTokenPre = tLoginDataPre.accessToken || tLoginDataPre.token;
    
    const teacherClassesResPre = await fetch('http://localhost:8088/api/teacher/classes', {
      headers: { 'Authorization': `Bearer ${teacherTokenPre}` }
    });
    const teacherClassesPre = await teacherClassesResPre.json();
    activeClassCode = teacherClassesPre[0].classCode;
    classId = teacherClassesPre[0].id;
    console.log(`✔ Active Class Code Retrieved: ${activeClassCode} (Class ID: ${classId})`);

    // STEP 1 – LOGIN AS PARENT
    console.log('\n--- STEP 1: PARENT LOGIN (dharun@gmail.com) ---');
    const pLoginRes = await fetch('http://localhost:8088/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'dharun@gmail.com', password: 'password123' })
    });
    const pLoginData = await pLoginRes.json();
    const parentToken = pLoginData.accessToken || pLoginData.token;
    parentId = pLoginData.userId || 4;
    console.log('✔ Parent Authenticated. User ID:', parentId);

    // STEP 2 – REUSE OR CREATE CHILD PROFILE
    console.log(`\n--- STEP 2: REUSE OR GET CHILD PROFILE (Sai) in Class ${activeClassCode} ---`);
    
    // First check existing students for this parent
    const existingStudentsRes = await fetch('http://localhost:8088/api/parent/students', {
      headers: { 'Authorization': `Bearer ${parentToken}` }
    });
    const existingStudents = await existingStudentsRes.json();
    let studentData = Array.isArray(existingStudents) ? existingStudents.find(s => s.name === 'Sai') : null;

    if (studentData && studentData.id) {
      console.log('✔ Existing Child Profile Found (Reused): ID', studentData.id);
      studentId = studentData.id;
      // Re-link if currently unlinked
      if (!studentData.classCode || studentData.classCode === 'N/A') {
        const relinkRes = await fetch(`http://localhost:8088/api/parent/student/${studentId}/class?classCode=${activeClassCode}`, {
          method: 'PUT',
          headers: { 'Authorization': `Bearer ${parentToken}` }
        });
        if (relinkRes.ok) {
          studentData = await relinkRes.json();
          console.log('✔ Student Re-linked to Class:', activeClassCode);
        }
      }
    } else {
      const createChildPayload = {
        name: 'Sai',
        dateOfBirth: '2016-01-05',
        gender: 'Male',
        heightCm: 125.0,
        weightKg: 25.0,
        bloodGroup: 'O+',
        classCode: activeClassCode,
        relationship: 'FATHER'
      };

      const createChildRes = await fetch('http://localhost:8088/api/parent/student', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${parentToken}`
        },
        body: JSON.stringify(createChildPayload)
      });
      studentData = await createChildRes.json();
      studentId = studentData.id;
      console.log('✔ New Student Created ID:', studentId);
    }
    console.log('✔ Student Name:', studentData.name);
    console.log('✔ Linked Class Code:', studentData.classCode || activeClassCode);

    // STEP 3 – VERIFY TEACHER LINKING
    console.log('\n--- STEP 3: VERIFY TEACHER LINKING (jothi@gmail.com) ---');
    const teacherToken = teacherTokenPre;
    console.log('✔ Teacher Token Reused for Roster Check');

    const teacherClassesRes = await fetch('http://localhost:8088/api/teacher/classes', {
      headers: { 'Authorization': `Bearer ${teacherToken}` }
    });
    const teacherClasses = await teacherClassesRes.json();
    console.log('  Teacher Classes Count:', teacherClasses.length);

    const teacherStudentsRes = await fetch(`http://localhost:8088/api/teacher/students?classCode=${activeClassCode}`, {
      headers: { 'Authorization': `Bearer ${teacherToken}` }
    });
    const teacherStudents = await teacherStudentsRes.json();
    console.log(`  Teacher Roster Count for ${activeClassCode}:`, teacherStudents.length);
    const foundSai = teacherStudents.find(s => s.id === studentId || s.name === 'Sai');
    console.log('✔ Sai Present in Teacher Roster:', !!foundSai);

    // STEP 4 – CREATE PRE-MEAL RECORD
    console.log('\n--- STEP 4: CREATE PRE-MEAL RECORD FOR SAI ---');
    const preMealPayload = {
      studentId: studentId,
      preMealImageUrl: '/uploads/premeal.png',
      boxLength: 21.0,
      boxWidth: 15.0,
      boxHeight: 6.5,
      foodItems: [
        { foodName: "Steamed Rice", quantity: "100g", calories: 250, proteinG: 5.0, carbsG: 55.0, fatG: 1.0, fiberG: 2.0 },
        { foodName: "Chicken Curry", quantity: "80g", calories: 220, proteinG: 20.0, carbsG: 5.0, fatG: 10.0, fiberG: 1.0 },
        { foodName: "Apple Slices", quantity: "50g", calories: 60, proteinG: 0.5, carbsG: 15.0, fatG: 0.2, fiberG: 3.0 }
      ]
    };

    const preMealRes = await fetch('http://localhost:8088/api/meals/pre-meal', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${parentToken}`
      },
      body: JSON.stringify(preMealPayload)
    });
    const mealData = await preMealRes.json();
    console.log('  Status Code:', preMealRes.status);
    mealId = mealData.id;
    console.log('✔ Meal ID Created in MySQL:', mealId);
    console.log('✔ Initial Meal Status:', mealData.status);

    // STEP 5 – VERIFY TEACHER RECEIVES MEAL
    console.log('\n--- STEP 5: VERIFY TEACHER RECEIVES MEAL ---');
    const todayMealsRes = await fetch(`http://localhost:8088/api/meals/today/class/${activeClassCode}`, {
      headers: { 'Authorization': `Bearer ${teacherToken}` }
    });
    const todayMeals = await todayMealsRes.json();
    const teacherMeal = todayMeals.find(m => m.id === mealId);
    console.log('✔ Teacher Receives Sai Meal Record:', !!teacherMeal);
    if (teacherMeal) {
      console.log('  Packed Foods Visible to Teacher:', teacherMeal.foodItems.map(f => f.foodName).join(', '));
    }

    // STEP 6 – TEACHER LOGS CONSUMPTION (75%)
    console.log('\n--- STEP 6: TEACHER LOGS CONSUMPTION (75%) ---');
    const quickRes = await fetch('http://localhost:8088/api/meals/consumption-quick', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${teacherToken}`
      },
      body: JSON.stringify({ mealId: mealId, overallConsumptionPercentage: 75 })
    });
    const quickData = await quickRes.json();
    console.log('  Status Code:', quickRes.status);
    console.log('✔ Updated Status in MySQL:', quickData.status);

    // STEP 7 – LEFTOVER IMAGE UPLOAD
    console.log('\n--- STEP 7: LEFTOVER IMAGE UPLOAD ---');
    const content = "fake leftover photo binary data";
    const blob = new Blob([content], { type: 'image/png' });
    const formData = new FormData();
    formData.append('file', blob, 'premeal.png');

    const leftoverRes = await fetch('http://localhost:8088/api/meals/' + mealId + '/leftover-image', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${teacherToken}` },
      body: formData
    });
    const leftoverData = await leftoverRes.json();
    console.log('  Status Code:', leftoverRes.status);
    console.log('✔ Final Meal Status in MySQL:', leftoverData.status);
    console.log('✔ Post Meal Image Stored URL:', leftoverData.postMealImageUrl);

    // STEP 8 – VERIFY PARENT VIEW
    console.log('\n--- STEP 8: VERIFY PARENT VIEW ---');
    const parentMealsRes = await fetch('http://localhost:8088/api/meals/student/' + studentId, {
      headers: { 'Authorization': `Bearer ${parentToken}` }
    });
    const parentMeals = await parentMealsRes.json();
    const finalParentMeal = parentMeals.find(m => m.id === mealId);
    console.log('✔ Parent Sees Meal Record:', !!finalParentMeal);
    console.log('✔ Parent Sees Final Status:', finalParentMeal.status);
    console.log('✔ Parent Sees Post Meal Image:', finalParentMeal.postMealImageUrl);

    console.log('\n=====================================================');
    console.log('REAL WORKFLOW DATA CREATION & AUDIT SUMMARY');
    console.log('=====================================================');
    console.log(`Parent ID : ${parentId}`);
    console.log(`Student ID: ${studentId} (Sai)`);
    console.log(`Class ID  : ${classId} (${activeClassCode})`);
    console.log(`Meal ID   : ${mealId}`);
    console.log('=====================================================\n');

  } catch (e) {
    console.error('Workflow Error:', e);
  }
}

runRealWorkflowDataCreation();
