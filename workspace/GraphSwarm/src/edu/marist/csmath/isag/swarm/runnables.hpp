#ifndef HPP_RUNNABLES
#define HPP_RUNNABLES

#include <thread>

/** */
template <typename Runnable>
class gthread
        : public std::thread
{
public:
        explicit gthread ( Runnable&& r )
                : std::thread(), m_task(std::move(r))
        { dynamic_cast<std::thread&>(*this) = std::thread(&Runnable::operator(),&m_task); }
private:
        Runnable m_task;
};

/** */
class othread
        : public std::thread
{
public:
        template <typename Runnable>
        explicit othread ( Runnable&& r )
                : std::thread(), m_task(new Runnable(std::move(r)))
        { dynamic_cast<std::thread&>(*this) = std::thread(&Runnable::operator(),static_cast<Runnable*>(m_task)); }
private:
        void* m_task;
};

/** */
class runnable
        : public std::thread
{
public:
        virtual void operator() () = 0;
        virtual void run() final { dynamic_cast<std::thread&>(*this) = std::thread(&runnable::operator(),this); }
};

#endif
