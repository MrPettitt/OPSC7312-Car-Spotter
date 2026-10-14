package com.example.carspotteropsc7312poe.data

import com.example.carspotteropsc7312poe.data.model.Car

/** Built-in cars used for the dashboard's "Car of the Day" and to seed the Knowledge screen. */
object SampleCars {
    val all = listOf(
        Car(
            "Porsche Cayenne",
            "Porsche's luxury performance SUV, launched in 2002. It pairs everyday practicality with sports-car handling.",
            "porsche"
        ),
        Car(
            "Mercedes GLE",
            "A mid-size luxury SUV from Mercedes-Benz, known for its comfortable ride, advanced technology and strong off-road ability.",
            "mercedes_gle"
        ),
        Car(
            "Ferrari F430",
            "A mid-engine V8 sports car built by Ferrari from 2004 to 2009, replacing the 360 Modena.",
            "ferrari"
        ),
        Car(
            "Rolls Royce Phantom",
            "The flagship Rolls-Royce saloon, hand-built in England and famous for its silent, smooth 'magic carpet' ride.",
            "rolls_royce"
        ),
        Car(
            "Bentley Bentayga",
            "Bentley's first SUV, launched in 2016, combining luxury craftsmanship with serious off-road capability.",
            "bentley_bentayga"
        )
    )
}
