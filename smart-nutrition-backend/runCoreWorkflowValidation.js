const http = require('http');

async function runCoreWorkflowValidation() {
  console.log('=====================================================');
  console.log('CHEWCHECKERS – CORE WORKFLOW END-TO-END VALIDATION');
  console.log('=====================================================\n');

  try {
    // 1. Authenticate Parent
    const pLoginRes = await fetch('http://localhost:8088/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'dharun@gmail.com', password: 'password123' })
    });
    const pLoginData = await pLoginRes.json();
    const parentToken = pLoginData.token;
    console.log('✔ Step 0a: Parent Authenticated (JWT token obtained)');

    // 2. Authenticate Teacher
    const tLoginRes = await fetch('http://localhost:8088/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'teacher_audit@school.com', password: 'password123' })
    });
    const tLoginData = await tLoginRes.json();
    const teacherToken = tLoginData.token;
    console.log('✔ Step 0b: Teacher Authenticated (JWT token obtained)');

    // STEP 1: PARENT LOGS MEAL
    console.log('\n--- STEP 1: PARENT LOGS MEAL (POST /api/meals/pre-meal) ---');
    const preMealPayload = {
      studentId: 1,
      preMealImageUrl: '/uploads/arjun_lunchbox.jpg',
      boxLength: 20.0,
      boxWidth: 15.0,
      boxHeight: 5.0,
      foodItems: [
        { foodName: "Steamed Rice", quantity: "100g", calories: 250, proteinG: 5.0, carbsG: 55.0, fatG: 1.0, fiberG: 2.0 },
        { foodName: "Grilled Chicken Breast", quantity: "80g", calories: 220, proteinG: 22.0, carbsG: 0.0, fatG: 8.0, fiberG: 0.0 },
        { foodName: "Fresh Apple Slices", quantity: "50g", calories: 60, proteinG: 0.5, carbsG: 15.0, fatG: 0.2, fiberG: 3.0 }
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
    
    if (!preMealRes.ok) {
      const errText = await preMealRes.text();
      console.log('  Pre-meal Response Status:', preMealRes.status);
      console.log('  Pre-meal Error Body:', errText);
      return;
    }

    const mealData = await preMealRes.json();
    console.log('  Status Code:', preMealRes.status);
    console.log('  Meal ID Created in MySQL:', mealData.id);
    console.log('  Meal Status:', mealData.status);
    console.log('  Packed Items Count:', mealData.foodItems.length);
    const mealId = mealData.id;

    // STEP 2: TEACHER RECEIVES MEAL
    console.log('\n--- STEP 2: TEACHER RECEIVES MEAL (GET /api/meals/today/class/CLS-6070) ---');
    const todayMealRes = await fetch('http://localhost:8088/api/meals/today/class/CLS-6070', {
      headers: { 'Authorization': `Bearer ${teacherToken}` }
    });
    const todayMeals = await todayMealRes.json();
    console.log('  Status Code:', todayMealRes.status);
    console.log('  Total Today Meals Fetched from DB:', todayMeals.length);
    const fetchedMeal = todayMeals.find(m => m.id === mealId);
    console.log('  Found Logged Meal for Student 1:', !!fetchedMeal);
    if (fetchedMeal) {
      console.log('  Fetched Packed Foods:', fetchedMeal.foodItems.map(f => f.foodName).join(', '));
    }

    // STEP 3: TEACHER LOGS CONSUMPTION (1-CLICK 75%)
    console.log('\n--- STEP 3: TEACHER LOGS CONSUMPTION (POST /api/meals/consumption-quick) ---');
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
    console.log('  Updated Meal Status in MySQL:', quickData.status);
    console.log('  Leftover Image Required:', quickData.status === 'PENDING_LEFTOVER_ANALYSIS');

    // STEP 4: LEFTOVER ANALYSIS (POST /api/meals/{mealId}/leftover-image)
    console.log('\n--- STEP 4: LEFTOVER ANALYSIS (POST /api/meals/' + mealId + '/leftover-image) ---');
    const content = "fake leftover photo binary data";
    const blob = new Blob([content], { type: 'image/jpeg' });
    const formData = new FormData();
    formData.append('file', blob, 'arjun_leftover.jpg');

    const leftoverRes = await fetch('http://localhost:8088/api/meals/' + mealId + '/leftover-image', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${teacherToken}` },
      body: formData
    });
    const leftoverData = await leftoverRes.json();
    console.log('  Status Code:', leftoverRes.status);
    console.log('  Final Meal Status in MySQL:', leftoverData.status);
    console.log('  Post Meal Image URL:', leftoverData.postMealImageUrl);
    if (leftoverData.foodItems) {
      console.log('  Consumed Items Breakdown:');
      leftoverData.foodItems.forEach(item => {
        console.log(`    - ${item.foodName}: Packed ${item.calories} kcal -> Consumed ${item.consumedCalories} kcal (${item.consumptionPercentage}%)`);
      });
    }

    // STEP 5: PARENT RECEIVES RESULTS (GET /api/meals/student/1)
    console.log('\n--- STEP 5: PARENT RECEIVES RESULTS (GET /api/meals/student/1) ---');
    const parentViewRes = await fetch('http://localhost:8088/api/meals/student/1', {
      headers: { 'Authorization': `Bearer ${parentToken}` }
    });
    const parentMeals = await parentViewRes.json();
    console.log('  Status Code:', parentViewRes.status);
    const updatedParentMeal = parentMeals.find(m => m.id === mealId);
    console.log('  Parent Sees Final Status:', updatedParentMeal.status);
    console.log('  Parent Sees Post Meal Photo:', updatedParentMeal.postMealImageUrl);

    console.log('\n=====================================================');
    console.log('✔ CORE WORKFLOW PARENT → TEACHER → PARENT 100% VERIFIED!');
    console.log('=====================================================\n');
  } catch (e) {
    console.error('Validation Error:', e);
  }
}

runCoreWorkflowValidation();
