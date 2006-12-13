//=========================================================================
//  FORCEDIRECTEDPARAMETERS.H - part of
//                  OMNeT++/OMNEST
//           Discrete System Simulation in C++
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2006 Andras Varga

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef __FORCEDIRECTEDPARAMETERS_H_
#define __FORCEDIRECTEDPARAMETERS_H_

#include <math.h>
#include "geometry.h"
#include "forcedirectedparametersbase.h"
#include "forcedirectedembedding.h"

class Body : public IBody {
    protected:
        Variable *variable;

        double mass;
    
        double charge;
    
        Rs size;

    private:
        void constructor(Variable *variable, double mass, double charge, Rs& size) {
            this->variable = variable;
            this->mass = mass;
            this->charge = charge;
            this->size = size;

            variable->addMass(mass);
        }

    public:
        Body(Variable *variable) {
            constructor(variable, 10, 1, Rs(10, 10));
        }

        Body(Variable *variable, double mass, double charge, Rs& size) {
            constructor(variable, mass, charge, size);
        }

        virtual const char *getClassName() {
            return "Body";
        }

        virtual Pt getPosition() {
            return variable->getPosition();
        }

        virtual Variable *getVariable() {
            return variable;
        }

        virtual double getMass() {
            return mass;
        }
        
        virtual double getCharge() {
            return charge;
        }

        virtual Rs& getSize() {
            return size;
        }
};

class RelativelyPositionedBody : public Body {
    protected:
        Pt relativePosition;

        IPositioned *anchor;

    private:
        void constructor(IPositioned *anchor, Pt& relativePosition) {
            this->anchor = anchor;
            this->relativePosition = relativePosition;
        }

    public:
        RelativelyPositionedBody(Variable *variable, Pt& relativePosition) : Body(variable) {
            constructor(variable, relativePosition);
        }

        RelativelyPositionedBody(Variable *variable, IPositioned *anchor, Pt& relativePosition) : Body(variable) {
            constructor(anchor, relativePosition);
        }

        virtual const char *getClassName() {
            return "RelativelyPositionedBody";
        }

        virtual Pt getPosition() {
            return Pt(anchor->getPosition()).add(relativePosition);
        }
};

class BorderBody : public Body {
    public:
        BorderBody(bool horizontal, double position) : Body(new Variable(Pt(horizontal ? NaN : position, horizontal ? position : NaN))) {
            if (horizontal)
                size = Rs(POSITIVE_INFINITY, 0);
            else
                size = Rs(0, POSITIVE_INFINITY);
        }

        ~BorderBody() {
            delete variable;
        }

        virtual const char *getClassName() {
            return "BorderBody";
        }
};

class IElectricRepeal : public IForceProvider {
    public:
        virtual IBody *getCharge1() = 0;
    
        virtual IBody *getCharge2() = 0;
};

class AbstractElectricRepeal : public IElectricRepeal {
    protected:
        IBody *charge1;
    
        IBody *charge2;

    public:
        AbstractElectricRepeal(IBody *charge1, IBody *charge2) {
            this->charge1 = charge1;
            this->charge2 = charge2;
        }

        virtual IBody *getCharge1() {
            return charge1;
        }
        
        virtual IBody *getCharge2() {
            return charge2;
        }
        
        virtual Pt getVector() = 0;

        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            Pt vector = getVector();
            double distance = vector.getLength();
            // TODO: find intersection with boxes and use the distance between the boxes
    //            double modifiedDistance = distance - vertex1.rs.getDiagonalLength() / 2 - vertex2.rs.getDiagonalLength() / 2;
            double modifiedDistance = distance;
            double force;
            
            if (modifiedDistance <= 0)
                force = maxForce;
            else
                force = getValidForce(embedding.electricRepealCoefficient * charge1->getCharge() * charge2->getCharge() / modifiedDistance / modifiedDistance);
            
            charge1->getVariable()->addForce(vector, +force, embedding.inspected);
            charge2->getVariable()->addForce(vector, -force, embedding.inspected);
        }
};

class ElectricRepeal : public AbstractElectricRepeal {
    public:
        ElectricRepeal(IBody *charge1, IBody *charge2) : AbstractElectricRepeal(charge1, charge2) {
        }

        virtual Pt getVector() {
            return Pt(charge1->getPosition()).subtract(charge2->getPosition());
        }

        virtual const char *getClassName() {
            return "ElectricRepeal";
        }
};

class VerticalElectricRepeal : public AbstractElectricRepeal {
    public:
        VerticalElectricRepeal(IBody *charge1, IBody *charge2) : AbstractElectricRepeal(charge1, charge2) {
        }

        virtual Pt getVector() {
            return Pt(0, charge1->getPosition().y - charge2->getPosition().y);
        }

        virtual const char *getClassName() {
            return "VerticalElectricRepeal";
        }
};

class HorizontalElectricRepeal : public AbstractElectricRepeal {
    public:
        HorizontalElectricRepeal(IBody *charge1, IBody *charge2) : AbstractElectricRepeal(charge1, charge2) {
        }

        virtual Pt getVector() {
            return Pt(charge1->getPosition().x - charge2->getPosition().x, 0);
        }

        virtual const char *getClassName() {
            return "HorizontalElectricRepeal";
        }
};

class ISpring : public IForceProvider {
    public:
        virtual double getSpringCoefficient() = 0;
        
        virtual double getReposeLength() = 0;
        
        virtual IBody *getBody1() = 0;
        
        virtual IBody *getBody2() = 0;
};

class AbstractSpring : public ISpring {
    protected:
        IBody *body1;
        
        IBody *body2;

        double springCoefficient;

        double reposeLength;

    private:
        void constructor(IBody *body1, IBody *body2, double springCoefficient, double reposeLength) {
            this->body1 = body1;
            this->body2 = body2;
            this->springCoefficient = springCoefficient;
            this->reposeLength = reposeLength;
        }

    public:
        AbstractSpring(IBody *body1, IBody *body2) {
            constructor(body1, body2, 1, 0);
        }
        
        AbstractSpring(IBody *body1, IBody *body2, double springCoefficient, double reposeLength) {
            constructor(body1, body2, springCoefficient, reposeLength);
        }

        double getSpringCoefficient() {
            return springCoefficient;
        }

        double getReposeLength() {
            return reposeLength;
        }

        virtual IBody *getBody1() {
            return body1;
        }

        virtual IBody *getBody2() {
            return body2;
        }
        
        virtual Pt getVector() = 0;
        
        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            Pt vector = getVector();
            double distance = vector.getLength() - reposeLength;

            if (distance != 0) {
                double force = getValidSignedForce(embedding.springCoefficient * springCoefficient * distance);
                
                body1->getVariable()->addForce(vector, -force, embedding.inspected);
                body2->getVariable()->addForce(vector, +force, embedding.inspected);
            }
        }
};

class Spring : public AbstractSpring {
    public:
        Spring(IBody *body1, IBody *body2) : AbstractSpring(body1, body2) {
        }
        
        Spring(IBody *body1, IBody *body2, double springCoefficient, double reposeLength) : AbstractSpring(body1, body2, springCoefficient, reposeLength) {
        }

        virtual Pt getVector() {
            return Pt(body1->getPosition()).subtract(body2->getPosition());
        }

        virtual const char *getClassName() {
            return "Spring";
        }
};

class VerticalSpring : public AbstractSpring {
    public:
        VerticalSpring(IBody *body1, IBody *body2) : AbstractSpring(body1, body2) {
        }
        
        VerticalSpring(IBody *body1, IBody *body2, double springCoefficient, double reposeLength) : AbstractSpring(body1, body2, springCoefficient, reposeLength){
        }

        virtual Pt getVector() {
            return Pt(0, body1->getPosition().y - body2->getPosition().y);
        }

        virtual const char *getClassName() {
            return "VerticalSpring";
        }
};

class HorizonalSpring : public AbstractSpring {
    public:
        HorizonalSpring(IBody *body1, IBody *body2) : AbstractSpring(body1, body2) {
        }
        
        HorizonalSpring(IBody *body1, IBody *body2, double springCoefficient, double reposeLength) : AbstractSpring(body1, body2, springCoefficient, reposeLength) {
        }

        virtual Pt getVector() {
            return Pt(body1->getPosition().x - body2->getPosition().x, 0);
        }

        virtual const char *getClassName() {
            return "HorizonalSpring";
        }
};

class Friction : public IForceProvider {
    public:
        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            const std::vector<Variable *>& variables = embedding.getVariables();

            for (std::vector<Variable *>::const_iterator it = variables.begin(); it != variables.end(); it++) {
                Variable *variable = *it;
                Pt vector(variable->getVelocity());
                vector.reverse();
                double vlen = variable->getVelocity().getLength();

                if (vlen != 0)
                    variable->addForce(vector, embedding.updatedFrictionCoefficient * vlen, embedding.inspected);
            }
        }    

        virtual const char *getClassName() {
            return "Friction";
        }
};

class BodyConstraint : public IForceProvider {
    protected:
        IBody *body;

    public:
        BodyConstraint(IBody *body) {
            this->body = body;
        }

        virtual IBody *getBody() {
            return body;
        }

        virtual void applyForces(const ForceDirectedEmbedding& embedding) = 0;
};

class PointConstraint : public BodyConstraint {
    protected:
        Pt constraint;
    
        double coefficient;
    
    public:
        PointConstraint(IBody *body, double coefficient, Pt constraint) : BodyConstraint(body) {
            this->coefficient = coefficient;
            this->constraint = constraint;
        }

        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            Pt vector(constraint);
            vector.subtract(body->getPosition());
            body->getVariable()->addForce(vector, coefficient * vector.getLength(), embedding.inspected);
        }

        virtual const char *getClassName() {
            return "PointConstraint";
        }
};

class LineConstraint : public BodyConstraint {
    protected:
        Ln constraint;
    
        double coefficient;

    public:
        LineConstraint(IBody *body, double coefficient, Ln constraint) : BodyConstraint(body) {
            this->coefficient = coefficient;
            this->constraint = constraint;
        }

        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            Pt position = body->getPosition();
            Pt vector(constraint.getClosestPoint(position));
            vector.subtract(position);
            body->getVariable()->addForce(vector, coefficient * vector.getLength(), embedding.inspected);
        }

        virtual const char *getClassName() {
            return "LineConstraint";
        }
};

class CircleConstraint : public BodyConstraint {
    protected:
        Cc constraint;
    
    protected:
        double coefficient;
    
    public:
        CircleConstraint(IBody *body, double coefficient, Cc constraint) : BodyConstraint(body) {
            this->coefficient = coefficient;
            this->constraint = constraint;
        }

        virtual void applyForces(const ForceDirectedEmbedding& embedding) {
            Pt position = body->getPosition();
            Pt vector(constraint.origin);
            vector.subtract(position);
            double power = coefficient * (constraint.origin.getDistance(position) - constraint.radius);

            body->getVariable()->addForce(vector, power, embedding.inspected);
        }

        virtual const char *getClassName() {
            return "CircleConstraint";
        }
};

#endif
