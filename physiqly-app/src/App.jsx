import React, { useState, useEffect, useRef } from 'react';

// --- Main App Component ---
export default function App() {
  const [activeTab, setActiveTab] = useState('signin');
  const [loggedInUser, setLoggedInUser] = useState(null);

  // --- Login/Register Form State ---
  const [loginForm, setLoginForm] = useState({ email: '', password: '' });
  const [registerForm, setRegisterForm] = useState({ firstName: '', lastName: '', email: '', password: '' });
  const [authMessage, setAuthMessage] = useState({ type: '', text: '' });

  // --- App Data State ---
  const [exercises, setExercises] = useState([]);
  const [dailyLog, setDailyLog] = useState(null);
  const [fitnessGoal, setFitnessGoal] = useState(null);

  // --- Event Handlers ---
  const handleAuthInputChange = (e, formSetter) => {
    const { name, value } = e.target;
    formSetter(prev => ({ ...prev, [name]: value }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setAuthMessage({ type: '', text: '' });
    try {
      const response = await fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registerForm),
      });
      if (!response.ok) throw new Error('Registration failed');
      setAuthMessage({ type: 'success', text: 'Registration successful! Please sign in.' });
      setActiveTab('signin');
    } catch (error) {
      setAuthMessage({ type: 'error', text: error.message || 'Registration failed' });
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setAuthMessage({ type: '', text: '' });
    try {
      const response = await fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginForm),
      });
      if (!response.ok) throw new Error('Invalid email or password');
      const user = await response.json();
      setLoggedInUser(user);
    } catch (error) {
      setAuthMessage({ type: 'error', text: error.message || 'Login failed' });
    }
  };

  const handleLogout = () => {
    setLoggedInUser(null);
    setActiveTab('signin');
    setExercises([]);
    setDailyLog(null);
    setFitnessGoal(null);
  };

  // --- Data Fetching Effect ---
  useEffect(() => {
    if (loggedInUser) {
      const fetchAllData = async () => {
        try {
          // Fetch exercises from workout-service (port 8081)
          const exercisesRes = await fetch('http://localhost:8081/api/exercises');
          if (exercisesRes.ok) setExercises(await exercisesRes.json());

          // Fetch calorie log from calorie-service (port 8082)
          const logRes = await fetch(`http://localhost:8082/api/logs/${loggedInUser.id}/today`);
          if (logRes.ok) setDailyLog(await logRes.json());
          
          // Fetch fitness goal from goal-service (port 8083)
          const goalRes = await fetch(`http://localhost:8083/api/goals/${loggedInUser.id}`);
          if (goalRes.ok && goalRes.status !== 204) {
            setFitnessGoal(await goalRes.json());
          } else {
             setFitnessGoal(null);
          }

        } catch (error) {
          console.error("Failed to fetch dashboard data:", error);
        }
      };
      fetchAllData();
    }
  }, [loggedInUser]);


  // --- Render Logic ---
  if (loggedInUser) {
    return <Dashboard 
             user={loggedInUser} 
             onLogout={handleLogout}
             initialExercises={exercises}
             initialDailyLog={dailyLog}
             initialGoal={fitnessGoal}
             setFitnessGoal={setFitnessGoal}
           />;
  }

  return (
    <div className="bg-stone-50 min-h-screen text-stone-800 font-sans">
      <Header />
      <main className="container mx-auto px-4 py-12 md:py-20">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div className="text-center md:text-left">
            <h1 className="text-4xl md:text-5xl font-bold text-indigo-900 leading-tight">Your Personal AI Fitness Coach</h1>
            <p className="mt-4 text-lg text-stone-600">Plan workouts, track calories, and achieve your fitness goals with a personalized plan.</p>
          </div>
          <Auth
            activeTab={activeTab}
            setActiveTab={setActiveTab}
            loginForm={loginForm}
            registerForm={registerForm}
            handleAuthInputChange={handleAuthInputChange}
            setLoginForm={setLoginForm}
            setRegisterForm={setRegisterForm}
            handleLogin={handleLogin}
            handleRegister={handleRegister}
            authMessage={authMessage}
          />
        </div>
      </main>
    </div>
  );
}

// --- Sub-Components ---

const Header = ({ user, onLogout }) => (
  <header className="bg-white/80 backdrop-blur-md sticky top-0 z-10 shadow-sm">
    <nav className="container mx-auto px-4 py-3 flex justify-between items-center">
      <span className="text-2xl font-bold text-indigo-900">Physiqly</span>
      <div>
        {user ? (
          <button onClick={onLogout} className="bg-indigo-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-indigo-700 transition-colors">
            Logout
          </button>
        ) : (
          <>
            <a href="#features" className="text-stone-600 hover:text-indigo-900 px-3 py-2">Features</a>
            <a href="#about" className="text-stone-600 hover:text-indigo-900 px-3 py-2">About</a>
          </>
        )}
      </div>
    </nav>
  </header>
);

const Auth = ({ activeTab, setActiveTab, ...props }) => {
  const { loginForm, registerForm, handleAuthInputChange, setLoginForm, setRegisterForm, handleLogin, handleRegister, authMessage } = props;
  return (
    <div className="bg-white p-8 rounded-xl shadow-lg border border-stone-200">
      <div className="flex border-b border-stone-200 mb-6">
        <button onClick={() => setActiveTab('signin')} className={`py-3 px-6 font-semibold transition-colors ${activeTab === 'signin' ? 'text-indigo-700 border-b-2 border-indigo-700' : 'text-stone-500'}`}>Sign In</button>
        <button onClick={() => setActiveTab('register')} className={`py-3 px-6 font-semibold transition-colors ${activeTab === 'register' ? 'text-indigo-700 border-b-2 border-indigo-700' : 'text-stone-500'}`}>Register</button>
      </div>
      {authMessage.text && (
        <div className={`p-3 rounded-md mb-4 text-sm ${authMessage.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
          {authMessage.text}
        </div>
      )}
      {activeTab === 'signin' ? (
        <form onSubmit={handleLogin}>
          <input type="email" name="email" value={loginForm.email} onChange={(e) => handleAuthInputChange(e, setLoginForm)} placeholder="Email" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <input type="password" name="password" value={loginForm.password} onChange={(e) => handleAuthInputChange(e, setLoginForm)} placeholder="Password" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <button type="submit" className="w-full bg-indigo-600 text-white font-semibold p-3 rounded-lg hover:bg-indigo-700 transition-colors">Sign In</button>
        </form>
      ) : (
        <form onSubmit={handleRegister}>
          <input type="text" name="firstName" value={registerForm.firstName} onChange={(e) => handleAuthInputChange(e, setRegisterForm)} placeholder="First Name" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <input type="text" name="lastName" value={registerForm.lastName} onChange={(e) => handleAuthInputChange(e, setRegisterForm)} placeholder="Last Name" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <input type="email" name="email" value={registerForm.email} onChange={(e) => handleAuthInputChange(e, setRegisterForm)} placeholder="Email" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <input type="password" name="password" value={registerForm.password} onChange={(e) => handleAuthInputChange(e, setRegisterForm)} placeholder="Password" required className="w-full p-3 border border-stone-300 rounded-lg mb-4 focus:ring-2 focus:ring-indigo-500 outline-none" />
          <button type="submit" className="w-full bg-indigo-600 text-white font-semibold p-3 rounded-lg hover:bg-indigo-700 transition-colors">Create Account</button>
        </form>
      )}
    </div>
  );
};

const Dashboard = ({ user, onLogout, initialExercises, initialDailyLog, initialGoal, setFitnessGoal }) => {
  return (
    <div className="bg-stone-50 min-h-screen text-stone-800 font-sans">
      <Header user={user} onLogout={onLogout} />
      <main className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-indigo-900 mb-6">Welcome back, {user.firstName}!</h1>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-8">
            <CalorieTracker initialLog={initialDailyLog} userId={user.id} />
            <WorkoutPlanner initialExercises={initialExercises} />
          </div>
          <div className="lg:col-span-1 space-y-8">
             <GoalPlanner initialGoal={initialGoal} userId={user.id} onGoalUpdate={setFitnessGoal} />
             <Reminders />
          </div>
        </div>
      </main>
    </div>
  );
};

const CalorieTracker = ({ initialLog, userId }) => {
  const [log, setLog] = useState(initialLog);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    setLog(initialLog);
  }, [initialLog]);
  
  const totalCalories = log ? log.breakfastCalories + log.lunchCalories + log.eveningSnackCalories + log.dinnerCalories : 0;

  const handleLogChange = (e) => {
    const { name, value } = e.target;
    setLog(prev => ({...prev, [name]: Number(value) || 0}));
  };

  const handleSaveChanges = async () => {
     try {
      const response = await fetch('http://localhost:8082/api/logs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(log),
      });
      if (!response.ok) throw new Error('Failed to save log');
      const updatedLog = await response.json();
      setLog(updatedLog);
      setIsEditing(false);
    } catch (error) {
      console.error("Error saving calorie log:", error);
    }
  }

  if (!log) return <div className="bg-white p-6 rounded-xl shadow-lg border border-stone-200">Loading Calorie Tracker...</div>;

  return (
    <div className="bg-white p-6 rounded-xl shadow-lg border border-stone-200">
      <h2 className="text-xl font-bold text-indigo-900 mb-4">Today's Calorie Tracker</h2>
      <div className="text-center mb-6">
        <p className="text-4xl font-bold text-indigo-700">{totalCalories} <span className="text-2xl text-stone-500">/ {log.calorieGoal}</span></p>
        <p className="text-stone-600">Total Calories Consumed</p>
      </div>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
        {['breakfast', 'lunch', 'eveningSnack', 'dinner'].map(meal => (
          <div key={meal}>
            <label className="capitalize text-sm font-semibold text-stone-600 block mb-1">{meal.replace('Snack', ' Snack')}</label>
            <input 
              type="number"
              name={`${meal}Calories`}
              value={log[`${meal}Calories`]}
              onChange={handleLogChange}
              disabled={!isEditing}
              className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none disabled:bg-stone-100"
            />
          </div>
        ))}
      </div>
       {isEditing ? (
          <button onClick={handleSaveChanges} className="w-full bg-indigo-600 text-white font-semibold p-3 rounded-lg hover:bg-indigo-700 transition-colors">Save Changes</button>
        ) : (
          <button onClick={() => setIsEditing(true)} className="w-full bg-stone-200 text-stone-700 font-semibold p-3 rounded-lg hover:bg-stone-300 transition-colors">Edit</button>
        )}
    </div>
  );
};

const WorkoutPlanner = ({ initialExercises }) => {
  const [exercises, setExercises] = useState(initialExercises);
  const [newExercise, setNewExercise] = useState({ name: '', description: '', targetMuscle: '', equipment: '', difficulty: 'Beginner' });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewExercise(prev => ({ ...prev, [name]: value }));
  };

  const handleAddExercise = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8081/api/exercises', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newExercise),
      });
      if (!response.ok) throw new Error('Failed to add exercise');
      const addedExercise = await response.json();
      setExercises(prev => [...prev, addedExercise]);
      setNewExercise({ name: '', description: '', targetMuscle: '', equipment: '', difficulty: 'Beginner' }); // Reset form
    } catch (error) {
      console.error("Error adding exercise:", error);
    }
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-lg border border-stone-200">
      <h2 className="text-xl font-bold text-indigo-900 mb-4">Workout Planner</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h3 className="font-semibold text-stone-700 mb-2">Available Exercises</h3>
          <ul className="space-y-3 max-h-96 overflow-y-auto pr-2">
            {exercises.length > 0 ? exercises.map(ex => (
              <li key={ex.id} className="p-3 bg-stone-50 rounded-lg border border-stone-200">
                <p className="font-bold">{ex.name}</p>
                <p className="text-sm text-stone-600">{ex.targetMuscle} | {ex.difficulty}</p>
              </li>
            )) : <p className="text-stone-500">No exercises found.</p>}
          </ul>
        </div>
        <form onSubmit={handleAddExercise} className="space-y-3">
          <h3 className="font-semibold text-stone-700">Add a New Exercise</h3>
          <input type="text" name="name" value={newExercise.name} onChange={handleInputChange} placeholder="Exercise Name" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
          <textarea name="description" value={newExercise.description} onChange={handleInputChange} placeholder="Description" className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none h-20"></textarea>
          <input type="text" name="targetMuscle" value={newExercise.targetMuscle} onChange={handleInputChange} placeholder="Target Muscle" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
          <input type="text" name="equipment" value={newExercise.equipment} onChange={handleInputChange} placeholder="Equipment" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
          <select name="difficulty" value={newExercise.difficulty} onChange={handleInputChange} className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white">
            <option>Beginner</option>
            <option>Intermediate</option>
            <option>Advanced</option>
          </select>
          <button type="submit" className="w-full bg-indigo-600 text-white font-semibold p-3 rounded-lg hover:bg-indigo-700 transition-colors">Add Exercise</button>
        </form>
      </div>
    </div>
  );
};

const GoalPlanner = ({ initialGoal, userId, onGoalUpdate }) => {
    const [goal, setGoal] = useState(initialGoal);
    const [form, setForm] = useState(initialGoal || {
        currentWeight: '',
        targetWeight: '',
        height: '',
        targetDate: '',
    });
    const [isEditing, setIsEditing] = useState(!initialGoal);

    useEffect(() => {
        setGoal(initialGoal);
        setForm(initialGoal || { currentWeight: '', targetWeight: '', height: '', targetDate: '' });
        setIsEditing(!initialGoal);
    }, [initialGoal]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleGoalSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = { ...form, userId };
            const response = await fetch('http://localhost:8083/api/goals', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });
            if (!response.ok) throw new Error('Failed to save goal');
            const updatedGoal = await response.json();
            onGoalUpdate(updatedGoal);
        } catch (error) {
            console.error("Error saving goal:", error);
        }
    };

    return (
        <div className="bg-white p-6 rounded-xl shadow-lg border border-stone-200">
            <div className="flex justify-between items-center mb-4">
                 <h2 className="text-xl font-bold text-indigo-900">Fitness Goal Planner</h2>
                 {goal && <button onClick={() => setIsEditing(!isEditing)} className="text-sm font-semibold text-indigo-600 hover:text-indigo-800">{isEditing ? 'Cancel' : 'Edit'}</button>}
            </div>
           
            {!isEditing && goal ? (
                <div className="space-y-4">
                    <div>
                        <p className="text-sm text-stone-500">Your Daily Plan</p>
                        <p className="text-2xl font-bold text-indigo-700">{goal.calculatedDailyCalories} <span className="text-lg">kcal</span></p>
                    </div>
                    <div className="grid grid-cols-3 gap-4 text-center">
                        <div>
                            <p className="font-bold">{goal.calculatedProteinGrams}g</p>
                            <p className="text-sm text-stone-500">Protein</p>
                        </div>
                        <div>
                            <p className="font-bold">{goal.calculatedCarbsGrams}g</p>
                            <p className="text-sm text-stone-500">Carbs</p>
                        </div>
                        <div>
                            <p className="font-bold">{goal.calculatedFatGrams}g</p>
                            <p className="text-sm text-stone-500">Fat</p>
                        </div>
                    </div>
                    <div className="pt-4 border-t border-stone-200">
                        <p><span className="font-semibold">Current Weight:</span> {goal.currentWeight} kg</p>
                        <p><span className="font-semibold">Target Weight:</span> {goal.targetWeight} kg</p>
                        <p><span className="font-semibold">Target Date:</span> {goal.targetDate}</p>
                    </div>
                </div>
            ) : (
                <form onSubmit={handleGoalSubmit} className="space-y-3">
                    <p className="text-sm text-stone-600">{goal ? 'Update your goal:' : 'Set a new goal to calculate your plan.'}</p>
                    <input type="number" step="0.1" name="currentWeight" value={form.currentWeight} onChange={handleInputChange} placeholder="Current Weight (kg)" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
                    <input type="number" step="0.1" name="targetWeight" value={form.targetWeight} onChange={handleInputChange} placeholder="Target Weight (kg)" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
                    <input type="number" name="height" value={form.height} onChange={handleInputChange} placeholder="Height (cm)" required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
                    <input type="date" name="targetDate" value={form.targetDate} onChange={handleInputChange} required className="w-full p-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none" />
                    <button type="submit" className="w-full bg-indigo-600 text-white font-semibold p-3 rounded-lg hover:bg-indigo-700 transition-colors">
                        {goal ? 'Update Plan' : 'Calculate My Plan'}
                    </button>
                </form>
            )}
        </div>
    );
};

const Reminders = () => {
    const [reminders, setReminders] = useState([
        { id: 1, text: 'Drink 250ml water', time: '09:00', complete: true },
        { id: 2, text: 'Mid-morning snack', time: '11:00', complete: false },
    ]);
    const [newReminderText, setNewReminderText] = useState('');
    const [newReminderTime, setNewReminderTime] = useState('');
    const notificationPermission = useRef(Notification.permission);

    // Effect to check for reminders every minute
    useEffect(() => {
        const interval = setInterval(() => {
            const now = new Date();
            const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
            
            reminders.forEach(reminder => {
                if (reminder.time === currentTime && !reminder.complete && notificationPermission.current === 'granted') {
                    new Notification('Physiqly Reminder', {
                        body: reminder.text,
                    });
                }
            });
        }, 60000); // Check every 60 seconds

        return () => clearInterval(interval);
    }, [reminders]);

    const handleAddReminder = (e) => {
        e.preventDefault();
        if (!newReminderText || !newReminderTime) return;
        const newReminder = {
            id: Date.now(),
            text: newReminderText,
            time: newReminderTime,
            complete: false
        };
        setReminders([...reminders, newReminder].sort((a, b) => a.time.localeCompare(b.time)));
        setNewReminderText('');
        setNewReminderTime('');
    };

    const toggleComplete = (id) => {
        setReminders(reminders.map(r => r.id === id ? { ...r, complete: !r.complete } : r));
    };

    const requestNotificationPermission = () => {
        if (!("Notification" in window)) {
            alert("This browser does not support desktop notification");
        } else if (Notification.permission !== "denied") {
            Notification.requestPermission().then(permission => {
                notificationPermission.current = permission;
            });
        }
    };
    
    return (
        <div className="bg-white p-6 rounded-xl shadow-lg border border-stone-200">
            <div className="flex justify-between items-center mb-4">
                 <h2 className="text-xl font-bold text-indigo-900">Daily Reminders</h2>
                 <button onClick={requestNotificationPermission} title="Enable Notifications" className="text-stone-400 hover:text-indigo-600">
                     <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
                 </button>
            </div>
            <form onSubmit={handleAddReminder} className="flex gap-2 mb-4">
                <input type="text" value={newReminderText} onChange={(e) => setNewReminderText(e.target.value)} placeholder="e.g., Drink water" className="flex-grow p-2 border border-stone-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none"/>
                <input type="time" value={newReminderTime} onChange={(e) => setNewReminderTime(e.target.value)} className="p-2 border border-stone-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 outline-none"/>
                <button type="submit" className="bg-indigo-600 text-white font-semibold px-3 rounded-lg hover:bg-indigo-700 transition-colors text-lg">+</button>
            </form>
            <ul className="space-y-2 max-h-48 overflow-y-auto pr-2">
                {reminders.map(reminder => (
                    <li key={reminder.id} onClick={() => toggleComplete(reminder.id)} className={`flex items-center justify-between p-2 rounded-lg cursor-pointer transition-colors ${reminder.complete ? 'bg-green-100 text-stone-500 line-through' : 'bg-stone-50 hover:bg-stone-100'}`}>
                        <div>
                            <p className="font-semibold">{reminder.text}</p>
                            <p className="text-xs">{reminder.time}</p>
                        </div>
                        <div className={`w-5 h-5 rounded-full border-2 ${reminder.complete ? 'bg-green-500 border-green-500' : 'border-stone-300'}`}></div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

